package com.seiko.common.app

import android.app.Application
import android.content.res.Configuration
import com.seiko.autosize.AutoSizeConfig
import com.seiko.common.util.helper.AutoAdaptStrategyFactory
import com.seiko.common.util.helper.providerAppManager
import com.seiko.common.util.prefs.initMMKV
import timber.log.Timber

/**
 * @description：插件自动加载该类，实现服务注册，需要指定在gradle.properties中application
 */
class ProviderApplication : InitComponent {

    override fun onCreate(application: Application) {
        Timber.tag("Provider").d("start register common.")

        // AutoSize
        val autoAdaptStrategyFactory = AutoAdaptStrategyFactory.get()
        AutoSizeConfig.init(application, strategy = autoAdaptStrategyFactory.create())

        // VLC
        application.providerAppManager()

        // 初始化MMKV
        application.initMMKV()
    }

    override fun onLowMemory() {

    }

    override fun onConfigurationChanged(newConfig: Configuration) {

    }

}