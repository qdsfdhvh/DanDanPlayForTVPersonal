package com.seiko.torrent.provider

import android.app.Application
import com.blankj.utilcode.util.LogUtils
import com.seiko.common.provider.IProviderApplication
import com.seiko.common.router.Routes
import com.seiko.common.service.TorrentInfoService
import com.seiko.torrent.di.downloadModule
import com.seiko.torrent.di.useCaseModule
import com.seiko.torrent.di.viewModelModule
import com.seiko.torrent.service.TorrentInfoServiceImpl
import org.koin.core.context.loadKoinModules

/**
 * @description：插件自动加载该类，实现服务注册
 */
class ProviderApplication : IProviderApplication {

    override fun onCreate(application: Application) {
        LogUtils.dTag("Provider", "start register torrent.")

        Routes.addService(TorrentInfoService::class.java.simpleName, TorrentInfoServiceImpl())

        loadKoinModules(listOf(downloadModule, useCaseModule, viewModelModule))
    }

    override fun onTerminate() {
        Routes.removeService(TorrentInfoService::class.java.simpleName)
    }
}