package com.seiko.player.provider

import android.app.Application
import com.seiko.common.provider.IProviderApplication
import com.seiko.player.di.playerModules
import com.seiko.player.media.initVlc
import org.koin.core.context.loadKoinModules
import timber.log.Timber

/**
 * @description：插件自动加载该类，实现服务注册
 */
class ProviderApplication : IProviderApplication {

    override fun onCreate(application: Application) {
        Timber.tag("Provider").d("start register player.")
        initVlc()
        loadKoinModules(playerModules)
    }

    override fun onTerminate() {

    }
}