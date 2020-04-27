package com.seiko.common.util.autosize.callback

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.seiko.common.util.autosize.AutoSizeConfig

/**
 * ================================================
 * {@link ActivityLifecycleCallbacksImpl} 可用来代替在 BaseActivity 中加入适配代码的传统方式
 * {@link ActivityLifecycleCallbacksImpl} 这种方案类似于 AOP, 面向接口, 侵入性低, 方便统一管理, 扩展性强, 并且也支持适配三方库的 {@link Activity}
 * <p>
 * Created by JessYan on 2018/8/8 14:32
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
internal class ActivityLifecycleCallbacksImpl(
    private var autoAdaptStrategy: AutoAdaptStrategy
) : Application.ActivityLifecycleCallbacks {

    /**
     * 让 Fragment 支持自定义适配参数
     */
    private val fragmentLifecycleCallbacks = FragmentLifecycleCallbacksImpl(autoAdaptStrategy)

    /**
     * 设置屏幕适配逻辑策略类
     *
     * @param autoAdaptStrategy {@link AutoAdaptStrategy}
     */
    fun setAutoAdaptStrategy(autoAdaptStrategy: AutoAdaptStrategy) {
        this.autoAdaptStrategy = autoAdaptStrategy
        fragmentLifecycleCallbacks.autoAdaptStrategy = autoAdaptStrategy
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (AutoSizeConfig.isCustomFragment) {
            if (activity is FragmentActivity) {
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
            }
        }
        autoAdaptStrategy.apply(activity, activity)
    }

    override fun onActivityStarted(activity: Activity) {
//        autoAdaptStrategy.apply(androidx.activity, androidx.activity)
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }
}