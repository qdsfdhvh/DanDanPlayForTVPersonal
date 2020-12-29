package com.seiko.common.initializer

import android.app.Application
import com.seiko.autosize.AutoSizeConfig
import com.seiko.common.util.fix.IMMLeaks
import com.seiko.common.util.helper.AutoAdaptStrategyFactory
import com.seiko.common.util.helper.providerAppManager
import com.seiko.common.util.prefs.initMMKV
import javax.inject.Inject

class CommonInitializer @Inject constructor(
    private val app: Application
) : AppInitializer() {

    override fun run() {
        // AutoSize
        val autoAdaptStrategyFactory = AutoAdaptStrategyFactory.get()
        AutoSizeConfig.init(app, strategy = autoAdaptStrategyFactory.create())

        // Activity栈管理
        app.providerAppManager()

        // 初始化MMKV
        app.initMMKV()

        // 修复输入法内存泄漏
        IMMLeaks.fixFocusedViewLeak(app)
    }
}