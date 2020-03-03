package com.seiko.player.media.vlc.extensions

import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.log10

internal fun Long.lengthToCategory(): String {
    val value: Int
    if (this == 0L) return "-"
    if (this < 60000) return "< 1 min"
    if (this < 600000) {
        value = floor((this / 60000).toDouble()).toInt()
        return "$value - ${(value + 1)} min"
    }
    return if (this < 3600000) {
        value = (10 * floor((this / 600000).toDouble())).toInt()
        "$value - ${(value + 10)} min"
    } else {
        value = floor((this / 3600000).toDouble()).toInt()
        "$value - ${(value + 1)} h"
    }
}

internal fun Long.readableSize(): String {
    val size: Long = this
    if (size <= 0) return "0"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1000.0)).toInt()
    return DecimalFormat("#,##0.#").format(size / Math.pow(1000.0, digitGroups.toDouble())) + " " + units[digitGroups]
}