package com.seiko.common.util.prefs

import androidx.preference.PreferenceDataStore
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun PreferenceDataStore.int(key: String, defValue: Int = 0): ReadWriteProperty<Any, Int> {
    return object : ReadWriteProperty<Any, Int> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Int {
            return getInt(key, defValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
            putInt(key, value)
        }
    }
}

fun PreferenceDataStore.long(key: String, defValue: Long = 0): ReadWriteProperty<Any, Long> {
    return object : ReadWriteProperty<Any, Long> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Long {
            return getLong(key, defValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
            putLong(key, value)
        }
    }
}

fun PreferenceDataStore.float(key: String, defValue: Float = 0f): ReadWriteProperty<Any, Float> {
    return object : ReadWriteProperty<Any, Float> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Float {
            return getFloat(key, defValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Float) {
            putFloat(key, value)
        }
    }
}

fun PreferenceDataStore.boolean(key: String, defValue: Boolean = false): ReadWriteProperty<Any, Boolean> {
    return object : ReadWriteProperty<Any, Boolean> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
            return getBoolean(key, defValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
            putBoolean(key, value)
        }
    }
}

fun PreferenceDataStore.string(key: String, defValue: String = ""): ReadWriteProperty<Any, String> {
    return object : ReadWriteProperty<Any, String> {
        override fun getValue(thisRef: Any, property: KProperty<*>): String {
            return getString(key, defValue)!!
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
            putString(key, value)
        }
    }
}

fun PreferenceDataStore.stringSet(key: String, defValue: Set<String> = emptySet()): ReadWriteProperty<Any, Set<String>> {
    return object : ReadWriteProperty<Any, Set<String>> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Set<String> {
            return getStringSet(key, defValue) ?: mutableSetOf()
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Set<String>) {
            putStringSet(key, value)
        }
    }
}