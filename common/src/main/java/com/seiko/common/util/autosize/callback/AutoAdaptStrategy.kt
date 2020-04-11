package com.seiko.common.util.autosize.callback

import android.app.Activity

/**
 * ================================================
 * 屏幕适配逻辑策略类, 可通过 {@link AutoSizeConfig#init(Application, boolean, AutoAdaptStrategy)}
 * 和 {@link AutoSizeConfig#setAutoAdaptStrategy(AutoAdaptStrategy)} 切换策略
 *
 * @see DefaultAutoAdaptStrategy
 * Created by JessYan on 2018/8/9 15:13
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
interface AutoAdaptStrategy {
    /**
     * 开始执行屏幕适配逻辑
     *
     * @param target   需要屏幕适配的对象 (可能是 {@link Activity} 或者 Fragment)
     * @param activity 需要拿到当前的 {@link Activity} 才能修改 {@link DisplayMetrics#density}
     */
    fun apply(target: Any, activity: Activity)
}