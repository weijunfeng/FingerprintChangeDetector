package com.zero.fingerprint.detector

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import com.zero.fingerprint.log.ILogger
import com.zero.fingerprint.platform.IFingerprintPlatform
import com.zero.fingerprint.storage.IPersistentStorage
import java.lang.reflect.InvocationTargetException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

/**
 * 解决在 android 10以上获取不到 GET_FINGERPRINT_ID 这个方法，导致无法处理指纹变更判断逻辑
 *
 * 该处理依赖指纹密钥创建，如果创建失败则无法判断指纹是否变更
 */
@RequiresApi(api = Build.VERSION_CODES.Q)
class FingerprintChangeDetectorImpl29(
    private val iLogger: ILogger,
    private val iPersistentStorage: IPersistentStorage,
    private val iFingerprintPlatform: IFingerprintPlatform
) : FingerprintChangeDetector {
    private var keyGenerator: KeyGenerator? = null
    private var keyStore: KeyStore? = null

    init {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
        } catch (e: KeyStoreException) {
            iLogger.e(TAG, e) { "create keyStore fail" }
        }
        try {
            keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        } catch (e: NoSuchAlgorithmException) {
            iLogger.e(TAG, e) { "create KeyGenerator fail" }
        } catch (e: NoSuchProviderException) {
            iLogger.e(TAG, e) { "create KeyGenerator fail" }
        }
    }

    /**
     * 创建cipher
     */
    @Throws(NoSuchPaddingException::class, NoSuchAlgorithmException::class)
    private fun createCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }

    /**
     * 指纹信息是否已经变更
     */
    override fun isChanged(): Boolean {
        val keyStore = this.keyStore
        if (keyGenerator == null || keyStore == null) {
            return false
        }
        if (!hasSavedEnrollIds()) {
            // 没有缓存注册的id, 直接返回false
            return false
        }
        val currEnrollFingerprintCount = enrollFingerprintCount()
        val enrollFingerprintCount = iPersistentStorage.getInt(ENROLL_FINGERPRINTS_COUNT, 0)
        if (enrollFingerprintCount != currEnrollFingerprintCount) {
            // 缓存的指纹数与实际不一致,返回true, 使用keyStore模式只能判断指纹修改和新加, 无法判断指纹删除
            return true
        }
        return try {
            keyStore.load(null)
            val key = keyStore.getKey(KEYSTORE_ALIAS, null) as? SecretKey
            if (key == null) {
                iLogger.e(TAG, null) { "KeyStore.getKey fail" }
                return false
            }
            createCipher().init(Cipher.ENCRYPT_MODE, key)
            false
        } catch (e: KeyPermanentlyInvalidatedException) {
            true
        } catch (e: Exception) {
            iLogger.e(TAG, e) { "check fingerprints modified exception fail" }
            false
        }
    }

    override fun hasSavedEnrollIds(): Boolean {
        return iPersistentStorage.getInt(ENROLL_FINGERPRINTS_COUNT, 0) > 0
    }

    override fun deleteEnrollIds() {
        iPersistentStorage.saveInt(ENROLL_FINGERPRINTS_COUNT, 0)
    }

    override fun saveEnrollIds(): Boolean {
        val keyGenerator = this.keyGenerator
        return if (keyGenerator == null || keyStore == null) {
            false
        } else try {
            val enrollFingerprintCount = enrollFingerprintCount()
            //创建新密钥
            val builder = KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setInvalidatedByBiometricEnrollment(true)
            keyGenerator.init(builder.build())
            keyGenerator.generateKey()
            // 保存当前指纹数目，使用密钥无法检测到指纹删除，只能检测到指纹新增或删除后新增变更
            iPersistentStorage.saveInt(ENROLL_FINGERPRINTS_COUNT, enrollFingerprintCount)
            true
        } catch (e: Exception) {
            iLogger.e(TAG, e) { "saveEnrollFingerprintsId fail" }
            false
        }
    }

    @Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        IllegalAccessException::class
    )
    private fun enrollFingerprintCount(): Int {
        return iFingerprintPlatform.getEnrolledFingerprints().size
    }

    companion object {
        private const val TAG = "FingerprintChangeCheckerImpl29"
        private const val KEYSTORE_ALIAS = "keyStoreAlias"
        private const val ENROLL_FINGERPRINTS_COUNT = "enroll_fingerprints_count"
    }
}