package com.seiko.torrent.provider

import android.app.Application
import com.seiko.common.provider.IProviderApplication
import com.seiko.torrent.di.*
import com.seiko.torrent.service.TorrentTaskService
import org.koin.core.context.loadKoinModules
import timber.log.Timber

/**
 * @description：插件自动加载该类，实现服务注册，需要指定在gradle.properties中application
 */
class ProviderApplication : IProviderApplication {

    override fun onCreate(application: Application) {
        Timber.tag("Provider").d("start register torrent.")

        loadKoinModules( listOf(
            // 数据库、网络请求、种子引擎
            dbModule, networkModule, torrentModule,
            // ...
            repositoryModule,
            // 实例
            useCaseModule,
            // viewModel
            viewModelModule
        ))

        TorrentTaskService.loadTrackers(application)
    }

    override fun onTerminate() {

    }
}