package com.seiko.common.util.autosize.model

/**
 * ================================================
 * {@link DisplayMetrics} 封装类
 * <p>
 * Created by JessYan on 2018/8/11 16:42
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
data class DisplayMetricsInfo(
    var density: Float = 0f,
    var densityDpi: Int = 0,
    var scaledDensity: Float = 0f,
    var xdpi: Float = 0f,
    var screenWidthDp: Int = 0,
    var screenHeightDp: Int = 0
) {
    override fun toString(): String {
        return "{" +
                "density=$density, " +
                "densityDpi=$densityDpi, " +
                "scaledDensity=$scaledDensity, " +
                "xdpi=$xdpi, " +
                "screenWidthDp=$screenWidthDp, " +
                "screenHeightDp=$screenHeightDp" +
                "}"
    }
}