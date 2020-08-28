package com.seiko.common.initializer

import android.app.Application
import android.content.Context
import android.os.StrictMode
import androidx.startup.Initializer
import com.seiko.autosize.AutoSizeConfig
import com.seiko.common.BuildConfig
import com.seiko.common.router.Navigator
import com.seiko.common.util.fix.IMMLeaks
import com.seiko.common.util.helper.AutoAdaptStrategyFactory
import com.seiko.common.util.helper.providerAppManager
import com.seiko.common.util.prefs.initMMKV

class CommonInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val app = context as Application

        // AutoSize
        val autoAdaptStrategyFactory = AutoAdaptStrategyFactory.get()
        AutoSizeConfig.init(app, strategy = autoAdaptStrategyFactory.create())

        // Activity栈管理
        app.providerAppManager()

        // 初始化MMKV
        app.initMMKV()

        // 初始化路由
        Navigator.init(app, BuildConfig.DEBUG)

        // 修复输入法内存泄漏
        IMMLeaks.fixFocusedViewLeak(app)

        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(
            TimberInitializer::class.java
        )
    }
}