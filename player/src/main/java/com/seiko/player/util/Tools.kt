package com.seiko.player.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

object Tools {

    private val sb = StringBuilder()
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

}