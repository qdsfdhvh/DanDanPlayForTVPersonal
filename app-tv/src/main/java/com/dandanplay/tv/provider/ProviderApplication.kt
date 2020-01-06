package com.dandanplay.tv.provider

import android.app.Application
import com.blankj.utilcode.util.LogUtils
import com.dandanplay.tv.di.viewModelModule
import com.seiko.common.provider.IProviderApplication
import org.koin.core.context.loadKoinModules

/**
 * @description：插件自动加载该类，实现服务注册
 */
class ProviderApplication : IProviderApplication {

    override fun registered(application: Application) {
        LogUtils.dTag("Provider", "start app-tv torrent.")
        loadKoinModules(viewModelModule)
    }

    override fun unregistered() {

    }
}