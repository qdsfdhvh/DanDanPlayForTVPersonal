package com.seiko.torrent.utils

import android.content.Context
import android.content.pm.PackageManager


internal fun getAppVersionName(context: Context?): String? {
    if (context == null) {
        return null
    }

    try {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        return info.versionName
    } catch (ignored: PackageManager.NameNotFoundException) {
    }
    return null
}

internal fun getAppVersionNumber(name: String?): String? {
    var versionName = name ?: return null
    val index = versionName.indexOf("-")
    if (index >= 0) versionName = versionName.substring(0, index)
    return versionName
}

internal fun getVersionComponents(name: String?): IntArray {
    val version = IntArray(3)
    var versionName = name ?: return version

    /* Discard additional information */
    versionName = getAppVersionNumber(versionName) ?: return version

    val components: Array<String> = versionName.split("\\.").toTypedArray()
    if (components.size < 2) return version

    try {
        version[0] = components[0].toInt()
        version[1] = components[1].toInt()
        if (components.size >= 3) version[2] = components[2].toInt()
    } catch (e: NumberFormatException) { /* Ignore */
    }
    return version
}