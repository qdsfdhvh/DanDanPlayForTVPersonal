package com.seiko.player.provider

import android.app.Application
import com.seiko.common.provider.IProviderApplication
import com.seiko.player.di.*
import org.koin.core.context.loadKoinModules
import timber.log.Timber

/**
 * @description：插件自动加载该类，实现服务注册，需要指定在gradle.properties中application
 */
class ProviderApplication : IProviderApplication {

    override fun onCreate(application: Application) {
        Timber.tag("Provider").d("start register player.")
        loadKoinModules(listOf(
            // 本地配置、数据库、网络请求
            prefModule, dbModule, networkModule,
            // 实例
            useCaseModule,
            // ...
            repositoryModule,
            // 播放器
            playModule,
            // viewModel
            viewModelModule
        ))
    }

    override fun onTerminate() {

    }
}