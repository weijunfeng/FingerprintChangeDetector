package com.zero.fingerprint.log

import android.util.Log

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
 * 控制台日志输出
 */
class ConsoleLogger : ILogger {
    override fun i(tag: String, message: () -> String) {
        Log.i(tag, message())
    }

    override fun e(tag: String, e: Exception?, message: () -> String) {
        Log.e(tag, message(), e)
    }
}