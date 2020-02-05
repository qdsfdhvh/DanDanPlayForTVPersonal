package com.seiko.player.util.extensions

import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

fun Long.readableSize(): String {
    val size: Long = this
    if (size <= 0) return "0"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1000.0)).toInt()
    return DecimalFormat("#,##0.#").format(
        size / 1000.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
}