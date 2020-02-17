package com.seiko.common.util.prefs


import android.app.Application
import android.os.Parcelable
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal fun Application.initMMKV() {
    MMKV.initialize(this@initMMKV)
}

fun createMMKVPreferenceDataStore(name: String): MmkvPreferenceDataStore {
    return MmkvPreferenceDataStore(name)
}

fun MMKV.int(key: String, defValue: Int = 0): ReadWriteProperty<Any, Int> {
    return object : ReadWriteProperty<Any, Int> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Int {
            return decodeInt(key, defValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
            encode(key, value)
        }
    }
}

fun MMKV.long(key: String, defValue: Long = 0): ReadWriteProperty<Any, Long> {
    return object : ReadWriteProperty<Any, Long> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Long {
            return decodeLong(key, defValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
            encode(key, value)
        }
    }
}

fun MMKV.float(key: String, defValue: Float = 0f): ReadWriteProperty<Any, Float> {
    return object : ReadWriteProperty<Any, Float> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Float {
            return decodeFloat(key, defValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Float) {
            encode(key, value)
        }
    }
}

fun MMKV.boolean(key: String, defValue: Boolean = false): ReadWriteProperty<Any, Boolean> {
    return object : ReadWriteProperty<Any, Boolean> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
            return decodeBool(key, defValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
            encode(key, value)
        }
    }
}

fun MMKV.string(key: String, defValue: String = ""): ReadWriteProperty<Any, String> {
    return object : ReadWriteProperty<Any, String> {
        override fun getValue(thisRef: Any, property: KProperty<*>): String {
            return decodeString(key, defValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
            encode(key, value)
        }
    }
}

fun MMKV.stringSet(key: String, defValue: Set<String> = emptySet()): ReadWriteProperty<Any, Set<String>> {
    return object : ReadWriteProperty<Any, Set<String>> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Set<String> {
            return decodeStringSet(key, defValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Set<String>) {
            encode(key, value)
        }
    }
}

inline fun <reified T: Parcelable> MMKV.parcelable(key: String, defValue: T): ReadWriteProperty<Any, T> {
    return object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            return decodeParcelable(key, T::class.java, defValue)!!
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            encode(key, value)
        }
    }
}