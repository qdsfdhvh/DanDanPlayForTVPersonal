package com.seiko.common.util.helper

import android.app.Activity
import android.content.res.Configuration
import com.seiko.common.util.AndroidDevices
import com.seiko.common.util.autosize.AutoSize
import com.seiko.common.util.autosize.AutoSizeConfig
import com.seiko.common.util.autosize.AutoSizeConfig.TAG
import com.seiko.common.util.autosize.callback.AutoAdaptStrategy
import timber.log.Timber

/**
 * 自定义屏幕适配逻辑策略
 * 手机端适配尺寸为360x640
 * 平板端适配尺寸为1280x720
 */
open class AppAutoAdaptStrategy : AutoAdaptStrategy {

    // 忽略适配
    interface Ignore

    companion object {
        private const val MOBILE_DESIGN_WIDTH_IN_DP = 360f
        private const val MOBILE_DESIGN_HEIGHT_IN_DP = 640f

        private const val TABLET_DESIGN_WIDTH_IN_DP = 1280f
        private const val TABLET_DESIGN_HEIGHT_IN_DP = 720f
    }

    override fun apply(target: Any, activity: Activity) {
        if (target is Ignore) return

        /*
         * 目前手机与平板的适配方法相似，但是仍然分开处理
         */
        if (AndroidDevices.isTablet) {
            applyWithTablet(target, activity)
        } else {
            applyWithMobile(target, activity)
        }
    }

    /**
     * 手机端尺寸适应
     */
    private fun applyWithMobile(target: Any, activity: Activity) {
        val sizeInDp: Float
        val screenSize: Int
        val isVertical = activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        if (isVertical) {
            sizeInDp = MOBILE_DESIGN_WIDTH_IN_DP
            screenSize = AutoSizeConfig.screenWidth
        } else {
            sizeInDp = MOBILE_DESIGN_HEIGHT_IN_DP
            screenSize = AutoSizeConfig.screenHeight
        }

        AutoSize.autoConvertDensity(activity, sizeInDp, screenSize)

        Timber.tag(TAG).d("Apply Mobile: %s, isVertical=%s, sizeInDp=%f, screenSize=%d",
            target.javaClass.name,
            isVertical,
            sizeInDp,
            screenSize)
    }

    /**
     * 平板端尺寸适应
     */
    private fun applyWithTablet(target: Any, activity: Activity) {
        val sizeInDp: Float
        val screenSize: Int
        val isVertical = activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        if (isVertical) {
            sizeInDp = TABLET_DESIGN_WIDTH_IN_DP
            screenSize = AutoSizeConfig.screenWidth
        } else {
            sizeInDp = TABLET_DESIGN_HEIGHT_IN_DP
            screenSize = AutoSizeConfig.screenHeight
        }
        AutoSize.autoConvertDensity(activity, sizeInDp, screenSize)

        Timber.tag(TAG).d("Apply Tablet: %s, isVertical=%s, sizeInDp=%f, screenSize=%d",
            target.javaClass.name,
            isVertical,
            sizeInDp,
            screenSize)
    }

}