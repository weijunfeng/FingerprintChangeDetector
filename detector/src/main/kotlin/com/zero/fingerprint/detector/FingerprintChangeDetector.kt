package com.zero.fingerprint.detector

import android.os.Build
import com.zero.fingerprint.log.ILogger
import com.zero.fingerprint.platform.IFingerprintPlatform
import com.zero.fingerprint.storage.IPersistentStorage

/**
 * 指纹变更检查器
 */
sealed interface FingerprintChangeDetector {
    /**
     * 存储手机中所有指纹id，在绑定指纹成功后调用
     *
     * @return true 成功 false失败
     */
    fun saveEnrollIds(): Boolean

    /**
     * 删除指纹id
     */
    fun deleteEnrollIds()

    /**
     * 是否已经保存指纹id
     *
     * @return true 成功 false 失败
     */
    fun hasSavedEnrollIds(): Boolean

    /**
     * 绑定指纹之后指纹是否发生了变更
     * @return true 变更 false 未变更
     */
    fun isChanged(): Boolean

    companion object {
        fun createDetector(
            iLogger: ILogger,
            iPersistentStorage: IPersistentStorage,
            iFingerprintPlatform: IFingerprintPlatform
        ): FingerprintChangeDetector {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return FingerprintChangeDetectorImpl29(iLogger, iPersistentStorage, iFingerprintPlatform)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return FingerprintChangeDetectorImpl23(iLogger, iPersistentStorage, iFingerprintPlatform)
            }
            return FingerprintChangeDetectorImpl
        }
    }
}