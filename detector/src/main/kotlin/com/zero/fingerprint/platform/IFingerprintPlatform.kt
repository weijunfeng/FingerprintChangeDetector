package com.zero.fingerprint.platform

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
 * 平台指纹功能
 */
interface IFingerprintPlatform {
    /**
     * 确定是否至少登记了一个指纹
     */
    fun hasEnrollFingerprints(): Boolean

    /**
     * 确定指纹硬件是否存在且功能正常
     */
    fun isHardwareDetected(): Boolean

    /**
     * 获取已登记的指纹列表
     */
    fun getEnrolledFingerprints(): List<Any>
}