package com.seiko.common.util.prefs

import androidx.preference.PreferenceDataStore
import com.tencent.mmkv.MMKV

class MmkvPreferenceDataStore(private val prefs: MMKV) : PreferenceDataStore() {

    constructor(name: String) : this(MMKV.mmkvWithID(name))

    fun allKeys(): Array<String>? {
        return prefs.allKeys()
    }

    fun remove(key: String?) {
        prefs.removeValueForKey(key)
    }

    override fun putInt(key: String?, value: Int) {
        prefs.putInt(key, value)
    }

    override fun getInt(key: String?, defValue: Int): Int {
        return prefs.getInt(key, defValue)
    }

    override fun putBoolean(key: String?, value: Boolean) {
        prefs.putBoolean(key, value)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return prefs.getBoolean(key, defValue)
    }

    override fun putFloat(key: String?, value: Float) {
        prefs.putFloat(key, value)
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        return prefs.getFloat(key, defValue)
    }

    override fun putLong(key: String?, value: Long) {
        prefs.putLong(key, value)
    }

    override fun getLong(key: String?, defValue: Long): Long {
        return prefs.getLong(key, defValue)
    }

    override fun putString(key: String?, value: String?) {
        prefs.putString(key, value)
    }

    override fun getString(key: String?, defValue: String?): String? {
        return prefs.getString(key, defValue)
    }

    override fun putStringSet(key: String?, values: MutableSet<String>?) {
        prefs.putStringSet(key, values)
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? {
        return prefs.getStringSet(key, defValues)
    }
}