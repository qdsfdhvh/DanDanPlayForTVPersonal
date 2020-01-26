package com.seiko.common.provider

import android.app.Application
import com.seiko.common.di.commonModules
import org.koin.core.context.loadKoinModules
import timber.log.Timber

/**
 * @description：插件自动加载该类，实现服务注册
 */
class ProviderApplication : IProviderApplication {

    override fun onCreate(application: Application) {
        Timber.tag("Provider").d("start register common.")

        loadKoinModules(commonModules)
    }

    override fun onTerminate() {

    }
}