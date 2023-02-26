package com.zero.fingerprint.detector

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
}