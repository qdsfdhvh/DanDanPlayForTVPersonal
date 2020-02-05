package com.seiko.common.util

import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import me.jessyan.autosize.AutoSizeConfig
import timber.log.Timber

/**
 * 动画 放大/缩放
 */
fun View.scaleAnimator(hasFocus: Boolean, focusScale: Float, duration: Long) {
    val scale = if (hasFocus) focusScale else 1.0f
    animate().scaleX(scale)
        .scaleY(scale)
        .setInterpolator(AccelerateInterpolator())
        .setDuration(duration)
}


fun getPercentWidthSize(value: Int): Int {
    val screenWidth = AutoSizeConfig.getInstance().screenWidth
    val designWidth = AutoSizeConfig.getInstance().designWidthInDp

    val res = value * screenWidth
    return if (res * designWidth == 0) {
        res / designWidth
    } else {
        res / designWidth + 1
    }
}

fun getPercentHeightSize(value: Int): Int {
    val screenHeight = AutoSizeConfig.getInstance().screenHeight
    val designHeight = AutoSizeConfig.getInstance().designHeightInDp

    val res = value * screenHeight
    return if (res * designHeight == 0) {
        res / designHeight
    } else {
        res / designHeight + 1
    }
}