package com.seiko.common.util.autosize

import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.content.res.Resources
import com.seiko.common.util.autosize.callback.ActivityLifecycleCallbacksImpl
import com.seiko.common.util.autosize.callback.AutoAdaptStrategy
import com.seiko.common.util.autosize.callback.DefaultAutoAdaptStrategy
import com.seiko.common.util.autosize.model.DisplayMetricsInfo
import com.seiko.common.util.autosize.unit.UnitsManager
import com.seiko.common.util.autosize.util.MiuiUtils
import com.seiko.common.util.autosize.util.ScreenUtils
import timber.log.Timber

/**
 * ================================================
 * AndroidAutoSize 参数配置类, 给 AndroidAutoSize 配置一些必要的自定义参数
 * <p>
 * Created by JessYan on 2018/8/8 09:58
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
object AutoSizeConfig {

    const val TAG = "AutoSizeConfig"

    /**
     * 应用
     */
    lateinit var app: Application
        private set

    /**
     * 用来管理 AndroidAutoSize 支持的所有单位, AndroidAutoSize 支持五种单位 (dp、sp、pt、in、mm)
     */
    internal var unitsManager = UnitsManager()
        private set

    /**
     * 默认尺寸
     */
    internal lateinit var initDisplayMetricsInfo: DisplayMetricsInfo

    /**
     * 设备的屏幕总宽度, 单位 px
     */
    var screenWidth: Int = 0

    /**
     * 设备的屏幕总高度, 单位 px, 如果 {@link #isUseDeviceSize} 为 {@code false}, 屏幕总高度会减去状态栏的高度
     */
    var screenHeight: Int = 0

    /**
     * {@link #mActivityLifecycleCallbacks} 可用来代替在 BaseActivity 中加入适配代码的传统方式
     * {@link #mActivityLifecycleCallbacks} 这种方案类似于 AOP, 面向接口, 侵入性低, 方便统一管理, 扩展性强, 并且也支持适配三方库的 {@link Activity}
     */
    private lateinit var activityLifecycleCallbacks: ActivityLifecycleCallbacksImpl

    /**
     * 是否让框架支持自定义 Fragment 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
     */
    var isCustomFragment = true

    private val componentCallbacks = object : ComponentCallbacks {
        override fun onConfigurationChanged(newConfig: Configuration) {
            if (newConfig.fontScale > 0) {
                val newScaledDensity = Resources.getSystem().displayMetrics.scaledDensity
                if (newScaledDensity != initDisplayMetricsInfo.scaledDensity) {
                    initDisplayMetricsInfo.scaledDensity = newScaledDensity
                    Timber.tag(TAG).d("initScaledDensity=%f on ConfigurationChanged",
                        initDisplayMetricsInfo.scaledDensity)
                }
            }
            val newScreenSize = ScreenUtils.getScreenSize(app)
            screenWidth = newScreenSize[0]
            screenHeight = newScreenSize[1]
        }

        override fun onLowMemory() {
            AutoSize.clear()
        }
    }

    /**
     * 初始化
     *
     * @param application   {@link Application}
     * @param strategy      {@link AutoAdaptStrategy}, 传 {@code null} 则使用 {@link DefaultAutoAdaptStrategy}
     */
    @JvmOverloads
    fun init(application: Application,
             strategy: AutoAdaptStrategy? = null,
             autoStart: Boolean = true) {
        this.app = application

        val screenSize = ScreenUtils.getScreenSize(app)
        screenWidth = screenSize[0]
        screenHeight = screenSize[1]
        Timber.tag(TAG).d("screenWidth=%d, screenHeight=%d", screenWidth, screenHeight)

        val displayMetrics = Resources.getSystem().displayMetrics
        val configuration = Resources.getSystem().configuration
        initDisplayMetricsInfo = DisplayMetricsInfo(
            density = displayMetrics.density,
            densityDpi = displayMetrics.densityDpi,
            scaledDensity = displayMetrics.scaledDensity,
            xdpi = displayMetrics.xdpi,
            screenWidthDp = configuration.screenWidthDp,
            screenHeightDp = configuration.screenHeightDp)
        Timber.tag(TAG).d("initDisplayMetricsInfo=$initDisplayMetricsInfo")

        activityLifecycleCallbacks = ActivityLifecycleCallbacksImpl(
            strategy ?: DefaultAutoAdaptStrategy())
        // 适配miui
        MiuiUtils.init(app)

        if (autoStart) start()
    }

    /**
     * 开始适配
     */
    fun start() {
        app.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        app.registerComponentCallbacks(componentCallbacks)
    }

    /**
     * 停止适配
     */
    fun stop() {
        app.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
        app.unregisterComponentCallbacks(componentCallbacks)
        AutoSize.clear()
    }

}