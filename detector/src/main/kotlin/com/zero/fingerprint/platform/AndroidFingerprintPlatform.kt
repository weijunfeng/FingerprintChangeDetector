package com.zero.fingerprint.platform

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import com.zero.fingerprint.log.ILogger


/*
 * Copyright (c) 2021 wjf510.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * android 平台指纹功能
 */
@TargetApi(Build.VERSION_CODES.M)
class AndroidFingerprintPlatform(context: Context, private val iLogger: ILogger) : IFingerprintPlatform {

    private val fingerprintManager: FingerprintManager? = context.getSystemService(FingerprintManager::class.java)

    override fun hasEnrollFingerprints(): Boolean {
        // 需要先判断一下硬件是否支持，有些设备上如果不判断这个，直接调用hasEnrolledFingerprints会去请求INTERACT_ACROSS_USERS，然后会因为没有权限而崩溃
        return isHardwareDetected() && fingerprintManager != null && fingerprintManager.hasEnrolledFingerprints()
    }

    override fun isHardwareDetected(): Boolean {
        return try {
            fingerprintManager != null && fingerprintManager.isHardwareDetected()
        } catch (ex: SecurityException) {
            iLogger.e("AndroidFingerprintPlatform", ex) {
                "call isHardwareDetected exception"
            }
            false
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun getEnrolledFingerprints(): List<Any> {
        if (fingerprintManager == null || !hasEnrollFingerprints()) {
            return listOf()
        }
        val getEnrollFingerprints = fingerprintManager.javaClass.getDeclaredMethod(GET_FINGERPRINTS_METHOD_NAME)
        getEnrollFingerprints.isAccessible = true
        return getEnrollFingerprints.invoke(fingerprintManager) as? List<Any> ?: listOf()
    }

    companion object {
        /**
         * 通过反射用来获取手机已录入指纹
         */
        private const val GET_FINGERPRINTS_METHOD_NAME = "getEnrolledFingerprints"
    }
}