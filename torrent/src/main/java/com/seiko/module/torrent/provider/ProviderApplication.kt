package com.seiko.module.torrent.provider

import android.app.Application
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.LogUtils
import com.seiko.common.provider.IProviderApplication
import com.seiko.common.router.Routes
import com.seiko.module.torrent.di.viewModelModule
import com.seiko.module.torrent.ui.TorrentMainActivity
import org.koin.core.context.loadKoinModules

/**
 * @description：插件自动加载该类，实现服务注册
 */
class ProviderApplication : IProviderApplication {

    override fun registered(application: Application) {
        LogUtils.dTag("Provider", "start register torrent.")

        loadKoinModules(viewModelModule)

        Routes.Torrent.register { context ->
            Intent(context, TorrentMainActivity::class.java)
        }
    }

    override fun unregistered() {

    }
}