package com.seiko.common.util.autosize

import android.app.Activity
import android.content.res.Configuration
import android.util.DisplayMetrics
import com.seiko.common.util.autosize.AutoSizeConfig.TAG
import com.seiko.common.util.autosize.model.DisplayMetricsInfo
import com.seiko.common.util.autosize.unit.Subunits
import com.seiko.common.util.autosize.util.MiuiUtils
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

/**
 * ================================================
 * AndroidAutoSize 用于屏幕适配的核心方法都在这里, 核心原理来自于 <a href="https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA">今日头条官方适配方案</a>
 * 此方案只要应用到 {@link Activity} 上, 这个 {@link Activity} 下的所有 Fragment、{@link Dialog}、
 * 自定义 {@link View} 都会达到适配的效果, 如果某个页面不想使用适配请让该 {@link Activity} 实现 {@link CancelAdapt}
 * <p>
 * 任何方案都不可能完美, 在成本和收益中做出取舍, 选择出最适合自己的方案即可, 在没有更好的方案出来之前, 只有继续忍耐它的不完美, 或者自己作出改变
 * 既然选择, 就不要抱怨, 感谢 今日头条技术团队 和 张鸿洋 等人对 Android 屏幕适配领域的的贡献
 * <p>
 * Created by JessYan on 2018/8/8 19:20
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
object AutoSize {

    /**
     * 尺寸缓存
     */
    private val mCache = ConcurrentHashMap<String, DisplayMetricsInfo>()

    /**
     * 清空缓存
     */
    fun clear() {
        mCache.clear()
    }

    /**
     * 这里是今日头条适配方案的核心代码, 核心在于根据当前设备的实际情况做自动计算并转换 {@link DisplayMetrics#density}、
     * {@link DisplayMetrics#scaledDensity}、{@link DisplayMetrics#densityDpi} 这三个值, 额外增加 {@link DisplayMetrics#xdpi}
     * 以支持单位 {@code pt}、{@code in}、{@code mm}
     *
     * @param activity      {@link Activity}
     * @param sizeInDp      设计图上的设计尺寸, 单位 dp, 如果 {@param isBaseOnWidth} 设置为 {@code true},
     *                      {@param sizeInDp} 则应该填写设计图的总宽度, 如果 {@param isBaseOnWidth} 设置为 {@code false},
     *                      {@param sizeInDp} 则应该填写设计图的总高度
     * // @param isBaseOnWidth 是否按照宽度进行等比例适配, {@code true} 为以宽度进行等比例适配, {@code false} 为以高度进行等比例适配
     * @param privateFontScale 区别于系统字体大小的放大比例, AndroidAutoSize 允许 APP 内部可以独立于系统字体大小之外，独自拥有全局调节 APP 字体大小的能力
     *                         当然, 在 APP 内您必须使用 sp 来作为字体的单位, 否则此功能无效, 将此值设为 0 则取消此功能
     * @param excludeFontScale 是否屏蔽系统字体大小对 AndroidAutoSize 的影响, 如果为 {@code true}, App 内的字体的大小将不会跟随系统设置中字体大小的改变
     * @see <a href="https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA">今日头条官方适配方案</a>
     */
    fun autoConvertDensity(activity: Activity, sizeInDp: Float, screenSize: Int, privateFontScale: Float = 0f, excludeFontScale: Boolean = false) {
        val key = "%s|%s|%s".format(sizeInDp, screenSize, AutoSizeConfig.initDisplayMetricsInfo.scaledDensity)
        var displayMetricsInfo = mCache[key]
        if (displayMetricsInfo == null) {
            val targetDensity = screenSize * 1.0f / sizeInDp
            val targetDensityDpi = (targetDensity * 160).toInt()
            var targetScaledDensity = targetDensity
            if (privateFontScale > 0) {
                targetScaledDensity *= privateFontScale
            } else if (!excludeFontScale) {
                targetScaledDensity *= AutoSizeConfig.initDisplayMetricsInfo.scaledDensity *
                        1.0f / AutoSizeConfig.initDisplayMetricsInfo.density
            }
            val targetScreenWidthDp = (AutoSizeConfig.screenWidth / targetDensity).toInt()
            val targetScreenHeightDp = (AutoSizeConfig.screenHeight / targetDensity).toInt()
            val targetXdpi = screenSize * 1.0f / sizeInDp
            displayMetricsInfo = DisplayMetricsInfo(
                density = targetDensity,
                densityDpi = targetDensityDpi,
                scaledDensity = targetScaledDensity,
                xdpi = targetXdpi,
                screenWidthDp = targetScreenWidthDp,
                screenHeightDp = targetScreenHeightDp)
        }
        setDensity(activity, displayMetricsInfo.density, displayMetricsInfo.densityDpi, displayMetricsInfo.scaledDensity, displayMetricsInfo.xdpi)
        setScreenSizeDp(activity, displayMetricsInfo.screenWidthDp, displayMetricsInfo.screenHeightDp)

        Timber.tag(TAG).d("The %s has been adapted! Info: sizeInDp=%f, screenSize=%d, %s",
            activity.javaClass.simpleName,
            sizeInDp,
            screenSize,
            displayMetricsInfo)
    }

    /**
     * 给几大 {@link DisplayMetrics} 赋值
     *
     * @param activity      {@link Activity}
     * @param density       {@link DisplayMetrics#density}
     * @param densityDpi    {@link DisplayMetrics#densityDpi}
     * @param scaledDensity {@link DisplayMetrics#scaledDensity}
     * @param xdpi          {@link DisplayMetrics#xdpi}
     */
    private fun setDensity(activity: Activity, density: Float, densityDpi: Int, scaledDensity: Float, xdpi: Float) {
        if (!MiuiUtils.isMiui) {
            val activityDisplayMetrics = activity.resources.displayMetrics
            setDensity(activityDisplayMetrics, density, densityDpi, scaledDensity, xdpi)

            val appDisplayMetrics = AutoSizeConfig.app.resources.displayMetrics
            setDensity(appDisplayMetrics, density, densityDpi, scaledDensity, xdpi)
        } else {
            val miuiActivityDisplayMetrics = MiuiUtils.getDisplayMetrics(activity.resources)
            if (miuiActivityDisplayMetrics != null) {
                setDensity(miuiActivityDisplayMetrics, density, densityDpi, scaledDensity, xdpi)
            }
            val miuiAppDisplayMetrics = MiuiUtils.getDisplayMetrics(AutoSizeConfig.app.resources)
            if (miuiAppDisplayMetrics != null) {
                setDensity(miuiAppDisplayMetrics, density, densityDpi, scaledDensity, xdpi)
            }
        }
    }

    /**
     * 赋值
     *
     * @param displayMetrics {@link DisplayMetrics}
     * @param density        {@link DisplayMetrics#density}
     * @param densityDpi     {@link DisplayMetrics#densityDpi}
     * @param scaledDensity  {@link DisplayMetrics#scaledDensity}
     * @param xdpi           {@link DisplayMetrics#xdpi}
     */
    private fun setDensity(displayMetrics: DisplayMetrics, density: Float, densityDpi: Int, scaledDensity: Float, xdpi: Float) {
        if (AutoSizeConfig.unitsManager.isSupportDP) {
            displayMetrics.density = density
            displayMetrics.densityDpi = densityDpi
        }
        if (AutoSizeConfig.unitsManager.isSupportSP) {
            displayMetrics.scaledDensity = scaledDensity
        }
        when(AutoSizeConfig.unitsManager.supportSubunits) {
            Subunits.NONE -> { /* do nothing */ }
            Subunits.PI -> displayMetrics.xdpi = xdpi * 72f
            Subunits.IN -> displayMetrics.xdpi = xdpi
            Subunits.MM -> displayMetrics.xdpi = xdpi * 25.4f
        }
    }

    /**
     * 给 {@link Configuration} 赋值
     *
     * @param activity       {@link Activity}
     * @param screenWidthDp  {@link Configuration#screenWidthDp}
     * @param screenHeightDp {@link Configuration#screenHeightDp}
     */
    private fun setScreenSizeDp(activity: Activity, screenWidthDp: Int, screenHeightDp: Int) {
        val unitsManager = AutoSizeConfig.unitsManager
        if (unitsManager.isSupportDP && unitsManager.isSupportScreenSizeDP) {
            val activityConfiguration = activity.resources.configuration
            setScreenSizeDp(activityConfiguration, screenWidthDp, screenHeightDp)

            val appConfiguration = AutoSizeConfig.app.resources.configuration
            setScreenSizeDp(appConfiguration, screenWidthDp, screenHeightDp)
        }
    }

    /**
     * Configuration赋值
     *
     * @param configuration  {@link Configuration}
     * @param screenWidthDp  {@link Configuration#screenWidthDp}
     * @param screenHeightDp {@link Configuration#screenHeightDp}
     */
    private fun setScreenSizeDp(configuration: Configuration, screenWidthDp: Int, screenHeightDp: Int) {
        configuration.screenWidthDp = screenWidthDp
        configuration.screenHeightDp = screenHeightDp
    }

}