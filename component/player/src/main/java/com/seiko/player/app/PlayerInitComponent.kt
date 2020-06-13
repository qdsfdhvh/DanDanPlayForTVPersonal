package com.seiko.player.app

import android.app.Application
import android.content.res.Configuration
import com.seiko.common.app.InitComponent
import com.seiko.player.di.*
import com.seiko.player.util.SmbUtils
import org.koin.core.context.loadKoinModules
import org.videolan.libvlc.Dialog
import org.videolan.tools.BitmapCache
import org.videolan.vlc.util.DialogDelegate
import timber.log.Timber

/**
 * @description：插件自动加载该类，实现服务注册，需要指定在gradle.properties中application
 */
class PlayerInitComponent : InitComponent
    , Dialog.Callbacks by DialogDelegate
    , VlcAppDelegate by AppVlcSetupDelegate() {

    override fun onCreate(application: Application) {
        Timber.tag("Provider").d("start register player.")
        loadKoinModules(listOf(
            // 本地配置、数据库、网络请求
            prefModule, dbModule, networkModule,
            // ...
            repositoryModule,
            // 实例
            useCaseModule
        ))

        application.setupApplication()
    }

    override fun onLowMemory() {
        BitmapCache.clear()
        SmbUtils.getInstance().clear()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        appContextProvider.updateContext()
    }
}