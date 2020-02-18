package com.seiko.common.util.extensions

import android.app.Activity
import android.util.DisplayMetrics

fun Activity.getScreenWidth() : Int {
    val dm = DisplayMetrics().also { windowManager.defaultDisplay.getMetrics(it) }
    return dm.widthPixels
}

fun Activity.getScreenHeight(): Int {
    val dm = DisplayMetrics().also { windowManager.defaultDisplay.getMetrics(it) }
    return dm.heightPixels
}