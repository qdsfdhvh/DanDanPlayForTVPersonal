package com.seiko.common.util

import android.content.Context
import android.content.res.Configuration
import com.seiko.common.util.autosize.util.ScreenUtils

object AndroidDevices {

    /**
     * 设备是否为平板
     */
    var isTablet: Boolean = false

    /**
     * 设备的屏幕总宽度, 单位 px
     */
    var screenWidth: Int = 0

    /**
     * 设备的屏幕总高度, 单位 px, 如果 {@link #isUseDeviceSize} 为 {@code false}, 屏幕总高度会减去状态栏的高度
     */
    var screenHeight: Int = 0

    fun init(context: Context) {
        isTablet = context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
        val newScreenSize = ScreenUtils.getScreenSize(context)
        screenWidth = newScreenSize[0]
        screenHeight = newScreenSize[1]
    }

}