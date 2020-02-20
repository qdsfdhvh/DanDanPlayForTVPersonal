package com.seiko.common.util

import me.jessyan.autosize.AutoSizeConfig

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

fun getRealTextSizeScale(scale: Float): Float {
    val screenHeight = AutoSizeConfig.getInstance().screenHeight
    val designHeight = AutoSizeConfig.getInstance().designHeightInDp
    return  designHeight * scale / screenHeight
}