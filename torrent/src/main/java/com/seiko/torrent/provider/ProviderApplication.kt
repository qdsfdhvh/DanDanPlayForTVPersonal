package com.seiko.torrent.provider

import android.app.Application
import com.blankj.utilcode.util.LogUtils
import com.seiko.common.provider.IProviderApplication
import com.seiko.torrent.di.downloadModule
import com.seiko.torrent.di.useCaseModule
import com.seiko.torrent.di.viewModelModule
import org.koin.core.context.loadKoinModules

/**
 * @description：插件自动加载该类，实现服务注册
 */
class ProviderApplication : IProviderApplication {

    override fun registered(application: Application) {
        LogUtils.dTag("Provider", "start register torrent.")

        loadKoinModules(listOf(downloadModule, useCaseModule, viewModelModule))
    }

    override fun unregistered() {

    }
}