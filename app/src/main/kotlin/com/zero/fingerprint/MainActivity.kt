package com.zero.fingerprint

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zero.fingerprint.detector.FingerprintChangeDetector
import com.zero.fingerprint.log.ConsoleLogger
import com.zero.fingerprint.platform.AndroidFingerprintPlatform
import com.zero.fingerprint.storage.SharedPreferencesStorage

class MainActivity : AppCompatActivity() {
    private val consoleLogger = ConsoleLogger()
    private val fingerprintChangeDetector by lazy {
        FingerprintChangeDetector.createDetector(
            consoleLogger,
            SharedPreferencesStorage(baseContext, "fingerprintChange"),
            AndroidFingerprintPlatform(baseContext, consoleLogger)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        val changed = fingerprintChangeDetector.isChanged()
        consoleLogger.i("MainActivity") {
            "fingerprint is changed == $changed"
        }
    }
}