package com.zero.fingerprint.detector

import android.annotation.SuppressLint
import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.zero.fingerprint.log.ILogger
import com.zero.fingerprint.platform.IFingerprintPlatform
import com.zero.fingerprint.storage.IPersistentStorage
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * android 23上检查指纹变更实现
 */
@RequiresApi(api = Build.VERSION_CODES.M)
class FingerprintChangeDetectorImpl23(
    private val iLogger: ILogger,
    private val iPersistentStorage: IPersistentStorage,
    private val iFingerprintPlatform: IFingerprintPlatform
) : FingerprintChangeDetector {
    /**
     * 存储手机中所有指纹id，在绑定指纹成功后调用
     */
    override fun saveEnrollIds(): Boolean {
        val ids = enrollFingerprintsId()
        iPersistentStorage.saveString(CACHE_ENROLL_IDS_KEY, ids.fingerprintIds())
        return true
    }

    private fun List<String>.fingerprintIds(): String {
        val jsonArray = JSONArray()
        for (id in this) {
            val jsonObject = JSONObject()
            jsonObject.put(JSON_FINGERPRINT_KEY, id)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    override fun deleteEnrollIds() {
        iPersistentStorage.saveString(CACHE_ENROLL_IDS_KEY, "")
    }

    override fun hasSavedEnrollIds(): Boolean {
        return cachedFingerprintsId().isNotEmpty()
    }

    /**
     * 绑定指纹之后指纹是否发生了变更
     */
    override fun isChanged(): Boolean {
        val cachedIds = cachedFingerprintsId()
        if (cachedIds.isEmpty()) {
            return false
        }
        val enrollIds = enrollFingerprintsId()
        if (cachedIds.size != enrollIds.size) {
            return true
        }
        val sortedLocalIds = cachedIds.sorted()
        val sortedEnrollIds = enrollIds.sorted()
        for (i in sortedLocalIds.indices) {
            if (sortedLocalIds[i] != sortedEnrollIds[i]) {
                return true
            }
        }
        return false
    }

    /**
     * 取出本地存储的指纹id
     */
    private fun cachedFingerprintsId(): List<String> {
        val fingerprintIdsToCache = iPersistentStorage.getString(CACHE_ENROLL_IDS_KEY, "")
        return fingerprintIdsToCache.fingerprintIds()
    }

    private fun String.fingerprintIds(): List<String> {
        if (TextUtils.isEmpty(this)) {
            return emptyList()
        }
        return try {
            val ids: MutableList<String> = ArrayList()
            val jsonArray = JSONArray(this)
            var jsonObject: JSONObject
            for (i in 0 until jsonArray.length()) {
                jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getString(JSON_FINGERPRINT_KEY)
                ids.add(id)
            }
            ids
        } catch (e: Exception) {
            iLogger.e(TAG, e) { "call String.fingerprintIds fail" }
            emptyList()
        }
    }

    /**
     * 注册的指纹id
     */
    @SuppressLint("DiscouragedPrivateApi")
    private fun enrollFingerprintsId(): List<String> {
        try {
            val enrollFingerprints = iFingerprintPlatform.getEnrolledFingerprints()
            val ids: MutableList<String> = ArrayList()
            for (enrollFingerprint in enrollFingerprints) {
                val getFingerprintId = enrollFingerprint.javaClass.getDeclaredMethod(GET_FINGERPRINT_ID)
                getFingerprintId.isAccessible = true
                val fingerprintId = getFingerprintId.invoke(enrollFingerprint) ?: continue
                ids.add(fingerprintId.toString())
            }
            return ids
        } catch (e: Exception) {
            iLogger.e(TAG, e) { "getEnrollFingerprintsId fail" }
        }
        return emptyList()
    }

    companion object {
        private const val TAG = "FingerprintChangeCheckerImpl23"

        /**
         * 存储的指纹id的json对应key
         */
        private const val JSON_FINGERPRINT_KEY = "json_fingerprint_key"

        /**
         * 用来获取指纹id的方法名
         */
        private const val GET_FINGERPRINT_ID = "getFingerId"

        /**
         * 保存指纹id的key
         */
        private const val CACHE_ENROLL_IDS_KEY = "enroll_ids_key"
    }
}