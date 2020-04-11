package com.seiko.common.util.autosize.util

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import java.lang.reflect.Field

object MiuiUtils {

    /**
     * 是否是 Miui 系统
     */
    var isMiui = false
        private set

    /**
     * Miui 系统中的 mTmpMetrics 字段
     */
    private var tmpMetricsField: Field? = null

    fun init(app: Application) {
        isMiui = isMiui(app)
        if (isMiui) {
            try {
                tmpMetricsField = Resources::class.java.getDeclaredField("mTmpMetrics")
                tmpMetricsField!!.isAccessible = true

            } catch (e: Exception) {
                tmpMetricsField = null
            }
        }
    }

    /**
     * 当前设备是否为Miui
     */
    private fun isMiui(context: Context): Boolean {
        return when(context.resources.javaClass.simpleName) {
            "MiuiResources", "XResources" -> true
            else -> false
        }
    }

    /**
     * 解决 MIUI 更改框架导致的 MIUI7 + Android5.1.1 上出现的失效问题 (以及极少数基于这部分 MIUI 去掉 ART 然后置入 XPosed 的手机)
     * 来源于: https://github.com/Firedamp/Rudeness/blob/master/rudeness-sdk/src/main/java/com/bulong/rudeness/RudenessScreenHelper.java#L61:5
     *
     * @param resources {@link Resources}
     * @return {@link DisplayMetrics}, 可能为 {@code null}
     */
    fun getDisplayMetrics(resources: Resources): DisplayMetrics? {
        if (!isMiui) return null
        return try {
            tmpMetricsField?.get(resources) as? DisplayMetrics
        } catch (e: Exception) {
            null
        }
    }
}