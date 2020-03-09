package com.seiko.player.app

import android.app.Application
import com.seiko.common.app.InitComponent
import com.seiko.player.di.*
import com.seiko.player.service.MediaParsingIntentService
import com.seiko.player.util.bitmap.BitmapCache
import org.koin.core.context.loadKoinModules
import org.videolan.libvlc.FactoryManager
import org.videolan.libvlc.LibVLCFactory
import org.videolan.libvlc.MediaFactory
import org.videolan.libvlc.interfaces.ILibVLCFactory
import org.videolan.libvlc.interfaces.IMediaFactory
import timber.log.Timber

/**
 * @description：插件自动加载该类，实现服务注册，需要指定在gradle.properties中application
 */
class PlayerInitComponent : InitComponent {

    override fun onCreate(application: Application) {
        Timber.tag("Provider").d("start register player.")
        loadKoinModules(listOf(
            // 本地配置、数据库、网络请求
            prefModule, dbModule, networkModule,
            // 播放器
            playModule,
            // ...
            repositoryModule,
            // 实例
            useCaseModule,
            // viewModel
            viewModelModule
        ))

        // VLC Service Loaders
        FactoryManager.registerFactory(IMediaFactory.factoryId, MediaFactory())
        FactoryManager.registerFactory(ILibVLCFactory.factoryId, LibVLCFactory())

        MediaParsingIntentService.startMediaLibrary(application, upgrade = true)
    }

    override fun onLowMemory() {
        BitmapCache.clear()
    }
}