package com.seiko.common.util.helper

import android.app.Activity
import android.content.res.Configuration
import com.seiko.common.util.AndroidDevices
import com.seiko.common.util.autosize.AutoSize
import com.seiko.common.util.autosize.AutoSizeConfig
import com.seiko.common.util.autosize.callback.AutoAdaptStrategy
import timber.log.Timber

// 忽略适配
interface AutoAdaptStrategyIgnore

interface AutoAdaptStrategyFactory {
    fun create(): AutoAdaptStrategy

    companion object {
        fun get(): AutoAdaptStrategyFactory {
            return AutoAdaptStrategyFactoryImpl()
        }
    }
}

/**
 * 自定义屏幕适配逻辑策略
 * 手机端适配尺寸为360x640
 * 平板端适配尺寸为1280x720
 */
internal class AutoAdaptStrategyFactoryImpl : AutoAdaptStrategyFactory {
    override fun create(): AutoAdaptStrategy {
        val strategy = if (AndroidDevices.isTablet) {
            TabletAutoAdaptStrategy()
        } else {
            MobileAutoAdaptStrategy()
        }
        return AutoAdaptStrategyProxy(strategy)
    }
}

/**
 * 处理一些适配策略共用的逻辑
 */
private class AutoAdaptStrategyProxy(
    private val strategy: AutoAdaptStrategy
) : AutoAdaptStrategy {
    override fun apply(target: Any, activity: Activity) {
        if (target is AutoAdaptStrategyIgnore
            || target.javaClass.name.endsWith("SupportRequestManagerFragment")
        ) return
        strategy.apply(target, activity)
    }
}

/**
 * 平板端尺寸适应
 */
private class TabletAutoAdaptStrategy : AutoAdaptStrategy {
    companion object {
        private const val TABLET_DESIGN_WIDTH_IN_DP = 1280f
        private const val TABLET_DESIGN_HEIGHT_IN_DP = 720f
    }
    override fun apply(target: Any, activity: Activity) {
        val sizeInDp: Float
        val screenSize: Int
        val isVertical = activity.isVertical()
        if (isVertical) {
            sizeInDp = TABLET_DESIGN_WIDTH_IN_DP
            screenSize = AutoSizeConfig.screenWidth
        } else {
            sizeInDp = TABLET_DESIGN_HEIGHT_IN_DP
            screenSize = AutoSizeConfig.screenHeight
        }
        AutoSize.autoConvertDensity(activity, sizeInDp, screenSize)

        Timber.tag(AutoSizeConfig.TAG).d(
            "Apply Tablet: %s, isVertical=%s, sizeInDp=%f, screenSize=%d",
            target.javaClass.name,
            isVertical,
            sizeInDp,
            screenSize)
    }
}

/**
 * 手机端尺寸适应
 */
private class MobileAutoAdaptStrategy : AutoAdaptStrategy {
    companion object {
        private const val MOBILE_DESIGN_WIDTH_IN_DP = 360f
        private const val MOBILE_DESIGN_HEIGHT_IN_DP = 640f
    }
    override fun apply(target: Any, activity: Activity) {
        val sizeInDp: Float
        val screenSize: Int
        val isVertical = activity.isVertical()
        if (isVertical) {
            sizeInDp = MOBILE_DESIGN_WIDTH_IN_DP
            screenSize = AutoSizeConfig.screenWidth
        } else {
            sizeInDp = MOBILE_DESIGN_HEIGHT_IN_DP
            screenSize = AutoSizeConfig.screenHeight
        }
        AutoSize.autoConvertDensity(activity, sizeInDp, screenSize)

        Timber.tag(AutoSizeConfig.TAG).d(
            "Apply Mobile: %s, isVertical=%s, sizeInDp=%f, screenSize=%d",
            target.javaClass.name,
            isVertical,
            sizeInDp,
            screenSize)
    }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun Activity.isVertical(): Boolean {
    return resources.configuration.orientation ==
            Configuration.ORIENTATION_PORTRAIT
}