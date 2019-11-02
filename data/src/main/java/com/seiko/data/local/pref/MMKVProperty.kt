package com.seiko.data.local.pref

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class MMKVProperty {

    protected fun MMKV.int(key: String, defValue: Int = 0): ReadWriteProperty<Any, Int> {
        return object : ReadWriteProperty<Any, Int> {
            override fun getValue(thisRef: Any, property: KProperty<*>): Int {
                return decodeInt(key, defValue)
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
                encode(key, value)
            }
        }
    }

    protected fun MMKV.long(key: String, defValue: Long = 0): ReadWriteProperty<Any, Long> {
        return object : ReadWriteProperty<Any, Long> {
            override fun getValue(thisRef: Any, property: KProperty<*>): Long {
                return decodeLong(key, defValue)
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
                encode(key, value)
            }
        }
    }

    protected fun MMKV.float(key: String, defValue: Float = 0f): ReadWriteProperty<Any, Float> {
        return object : ReadWriteProperty<Any, Float> {
            override fun getValue(thisRef: Any, property: KProperty<*>): Float {
                return decodeFloat(key, defValue)
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: Float) {
                encode(key, value)
            }
        }
    }

    protected fun MMKV.boolean(key: String, defValue: Boolean = false): ReadWriteProperty<Any, Boolean> {
        return object : ReadWriteProperty<Any, Boolean> {
            override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
                return decodeBool(key, defValue)
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
                encode(key, value)
            }
        }
    }

    protected fun MMKV.string(key: String, defValue: String = ""): ReadWriteProperty<Any, String> {
        return object : ReadWriteProperty<Any, String> {
            override fun getValue(thisRef: Any, property: KProperty<*>): String {
                return decodeString(key, defValue)
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
                encode(key, value)
            }
        }
    }

    protected fun MMKV.stringSet(key: String, defValue: Set<String> = emptySet()): ReadWriteProperty<Any, Set<String>> {
        return object : ReadWriteProperty<Any, Set<String>> {
            override fun getValue(thisRef: Any, property: KProperty<*>): Set<String> {
                return decodeStringSet(key, defValue)
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: Set<String>) {
                encode(key, value)
            }
        }
    }

    protected inline fun <reified T: Parcelable> MMKV.parcelable(key: String, defValue: T): ReadWriteProperty<Any, T> {
        return object : ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T {
                return decodeParcelable(key, T::class.java, defValue)!!
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
                encode(key, value)
            }
        }
    }

//    protected fun <T> MMKV.list(key: String, defValue: List<T>, gson: Gson): ReadWriteProperty<Any, List<T>> {
//        return object : ReadWriteProperty<Any, List<T>> {
//
//            private var cache: List<T>? = null
//
//            override fun getValue(thisRef: Any, property: KProperty<*>): List<T> {
//                cache?.let { return it }
//                val s = getString(key, "")!!
//                return if (s.isBlank()) {
//                    defValue
//                } else {
//                    val newValue = gson.fromJson<List<T>>(s, List::class.java)
//                    cache = newValue
//                    newValue
//                }
//            }
//
//            override fun setValue(thisRef: Any, property: KProperty<*>, value: List<T>) {
//                putString(key, gson.toJson(value))
//                cache = value
//            }
//        }
//    }

}