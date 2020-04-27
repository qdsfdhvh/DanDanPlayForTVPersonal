package com.seiko.common.util.autosize.util

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager

internal object ScreenUtils {

    /**
     * 获取当前的屏幕尺寸
     *
     * @param context {@link Context}
     * @return 屏幕尺寸
     */
    fun getScreenSize(context: Context): IntArray {
        val size = IntArray(2)
        val w = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return size
        val display = w.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)

        size[0] = metrics.widthPixels
        size[1] = metrics.heightPixels
        return size
    }

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(): Int {
        var result = 0
        try {
            val resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = Resources.getSystem().getDimensionPixelSize(resourceId)
            }
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
        return result
    }

}