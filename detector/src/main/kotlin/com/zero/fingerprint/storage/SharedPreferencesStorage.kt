package com.zero.fingerprint.storage

import android.content.Context

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
 * [android.content.SharedPreferences]存储
 */
class SharedPreferencesStorage(
    private val context: Context, private val name: String,
    private val keyProxy: (key: String) -> String = {
        it
    }
) : IPersistentStorage {
    private val sharedPreferences by lazy {
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    override fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(keyProxy(key), value).apply()
    }

    override fun saveInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(keyProxy(key), value).apply()
    }

    override fun getString(key: String, defValue: String): String {
        return sharedPreferences.getString(keyProxy(key), defValue) ?: defValue
    }

    override fun getInt(key: String, defValue: Int): Int {
        return sharedPreferences.getInt(keyProxy(key), defValue)
    }
}