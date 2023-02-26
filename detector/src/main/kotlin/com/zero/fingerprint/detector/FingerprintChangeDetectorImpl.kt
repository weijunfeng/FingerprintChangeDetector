package com.zero.fingerprint.detector

object FingerprintChangeDetectorImpl : FingerprintChangeDetector {
    override fun saveEnrollIds(): Boolean {
        return false
    }

    override fun deleteEnrollIds() {
        // ignore
    }

    override fun hasSavedEnrollIds(): Boolean {
        return false
    }

    override fun isChanged(): Boolean = false
}