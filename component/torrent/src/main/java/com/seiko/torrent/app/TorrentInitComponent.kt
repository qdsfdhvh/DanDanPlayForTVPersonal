package com.seiko.torrent.app

import android.app.Application
import android.content.res.Configuration
import com.seiko.common.app.InitComponent
import com.seiko.torrent.service.TorrentTaskService
import timber.log.Timber

/**
 * @description：插件自动加载该类，实现服务注册，需要指定在gradle.properties中application
 */
class TorrentInitComponent : InitComponent {

    override fun onCreate(application: Application) {
        Timber.tag("Provider").d("start register torrent.")
        TorrentTaskService.loadTrackers(application)
    }

    override fun onLowMemory() {

    }

    override fun onConfigurationChanged(newConfig: Configuration) {

    }
}