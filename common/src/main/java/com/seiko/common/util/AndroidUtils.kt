package com.seiko.common.util

import android.os.Build

object AndroidUtils {
    val isPOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    val isOOrLater = isPOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    val isNougatMR1OrLater = isOOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
    val isNougatOrLater = isNougatMR1OrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    val isMarshMallowOrLater = isNougatOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    val isLolliPopOrLater = isMarshMallowOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    val isKitKatOrLater = isLolliPopOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    val isJellyBeanMR2OrLater = isKitKatOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
}