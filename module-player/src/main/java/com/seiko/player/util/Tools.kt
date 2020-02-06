package com.seiko.player.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

object Tools {

    private val format = NumberFormat.getInstance(Locale.US) as DecimalFormat

    init {
        format.applyPattern("00")
    }

    /**
     * Convert time to a string
     * @param millis e.g.time/length from file
     * @return string (hh:)mm:ss
     */
    fun millisToString(millis: Long): String? {
        val totalSeconds = (millis / 1000)
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60
        return if (minutes > 99) {
            String.format("%d:%02d", minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    /**
     * Convert size to a string
     * @param size file size
     * @return string 213MB
     */
    fun sizeToString(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1000.0)).toInt()
        return DecimalFormat("#,##0.#").format(
            size / 1000.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }

}