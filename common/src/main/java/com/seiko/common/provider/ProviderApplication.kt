package com.seiko.common.provider

import android.app.Application
import com.blankj.utilcode.util.LogUtils
import com.seiko.core.di.dataModule
import org.koin.core.context.loadKoinModules

/**
 * @description：插件自动加载该类，实现服务注册
 */
class ProviderApplication : IProviderApplication {

    override fun onCreate(application: Application) {
        LogUtils.dTag("Provider", "start register common.")

        loadKoinModules(dataModule)
    }

    override fun onTerminate() {

    }
}