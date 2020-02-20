package com.seiko.player.util

object RecyclerViewUtils {

    fun getItemWidth(screenWidth: Int, nbColumns: Int, spacing: Int): Int {
        return ((screenWidth - (spacing * (nbColumns + 1))).toFloat() / nbColumns).toInt()
    }

    fun getColumns(screenWidth: Int, itemWidth: Int, spacing: Int): Int {
        return ((screenWidth - spacing).toFloat() / (itemWidth + spacing)).toInt()
    }
}