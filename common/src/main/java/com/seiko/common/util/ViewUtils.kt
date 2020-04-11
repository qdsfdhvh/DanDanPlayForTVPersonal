package com.seiko.common.util

import com.seiko.common.util.autosize.AutoSizeConfig


//fun getPercentWidthSize(value: Int): Int {
//    val screenWidth = AutoSizeConfig.screenWidth
//    val designWidth = AutoSizeConfig.designWidthInDp
//
//    val res = value * screenWidth
//    return if (res * designWidth == 0) {
//        res / designWidth
//    } else {
//        res / designWidth + 1
//    }
//}
//
//fun getPercentHeightSize(value: Int): Int {
//    val screenHeight = AutoSizeConfig.screenHeight
//    val designHeight = AutoSizeConfig.designHeightInDp
//
//    val res = value * screenHeight
//    return if (res * designHeight == 0) {
//        res / designHeight
//    } else {
//        res / designHeight + 1
//    }
//}

//fun getRealTextSizeScale(scale: Float): Float {
//    val screenHeight = AutoSizeConfig.screenHeight
//    val designHeight = AutoSizeConfig.designHeightInDp
//    return  designHeight * scale / screenHeight
//}