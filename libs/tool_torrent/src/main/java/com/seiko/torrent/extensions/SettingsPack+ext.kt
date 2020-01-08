package com.seiko.torrent.extensions

import org.libtorrent4j.SettingsPack
import org.libtorrent4j.swig.settings_pack

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
internal inline fun SettingsPack.getString(type: settings_pack.string_types): String {
    return getString(type.swigValue())
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
internal inline fun SettingsPack.getBoolean(type: settings_pack.bool_types): Boolean {
    return getBoolean(type.swigValue())
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
internal inline fun SettingsPack.getInteger(type: settings_pack.int_types): Int {
    return getInteger(type.swigValue())
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
internal inline fun SettingsPack.setString(type: settings_pack.string_types, value: String) {
    setString(type.swigValue(), value)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
internal inline fun SettingsPack.setBoolean(type: settings_pack.bool_types, value: Boolean) {
    setBoolean(type.swigValue(), value)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
internal inline fun SettingsPack.setInteger(type: settings_pack.int_types, value: Int) {
    setInteger(type.swigValue(), value)
}