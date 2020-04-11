package com.seiko.common.util.autosize.unit

import androidx.annotation.IntDef

/**
 * ================================================
 * AndroidAutoSize 支持一些在 Android 系统上比较少见的单位作为副单位, 用于规避修改 {@link DisplayMetrics#density}
 * 所造成的对于其他使用 dp 布局的系统控件或三方库控件的不良影响
 * <p>
 * Created by JessYan on 2018/8/28 10:27
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
@IntDef(Subunits.NONE, Subunits.PI, Subunits.IN, Subunits.MM)
@Retention(AnnotationRetention.SOURCE)
annotation class Subunits {
    companion object {
        /**
         * 不使用副单位
         */
        const val NONE = 0
        /**
         * 单位 pt
         *
         * @see android.util.TypedValue#COMPLEX_UNIT_PT
         */
        const val PI = 1
        /**
         * 单位 in
         *
         * @see android.util.TypedValue#COMPLEX_UNIT_IN
         */
        const val IN = 2
        /**
         * 单位 mm
         *
         * @see android.util.TypedValue#COMPLEX_UNIT_MM
         */
        const val MM = 3
    }
}