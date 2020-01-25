package com.dandanplay.tv.provider

import android.app.Application
import com.dandanplay.tv.di.useCaseModule
import com.dandanplay.tv.di.viewModelModule
import com.seiko.common.provider.IProviderApplication
import org.koin.core.context.loadKoinModules
import timber.log.Timber

/**
 * @description：插件自动加载该类，实现服务注册
 */
class ProviderApplication : IProviderApplication {

    override fun onCreate(application: Application) {
        Timber.tag("Provider").d("start app-tv torrent.")
        loadKoinModules(listOf(useCaseModule, viewModelModule))
    }

    override fun onTerminate() {

    }
}