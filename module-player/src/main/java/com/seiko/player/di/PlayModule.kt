package com.seiko.player.di

import android.content.Context
import com.seiko.danma.DanmakuEngine
import com.seiko.danma.IDanmakuEngine
import com.seiko.player.data.comments.VideoHistoryRepository
import com.seiko.player.data.prefs.PrefDataSource
import com.seiko.player.media.option.DanmaOptions
import com.seiko.player.util.constants.PLAYER_DATA_DIR
import com.seiko.player.util.constants.PLAYER_THUMBNAIL_DIR
import com.seiko.player.util.bitmap.ImageLoader
import com.seiko.player.util.bitmap.ThumbnailsProvider
import com.seiko.player.media.vlc.control.VlcPlayerController
import com.seiko.player.media.vlc.control.VlcPlayerListManager
import com.seiko.player.media.vlc.control.VlcLibManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.videolan.medialibrary.interfaces.Medialibrary
import java.io.File

val playModule = module {
    single(named(PLAYER_DATA_DIR)) { createPlayerDataDir(androidContext()) }
    single(named(PLAYER_THUMBNAIL_DIR)) { createPlayerThumbnailDir(get(named(PLAYER_DATA_DIR))) }

    single { createDanmakuEngine() }

    single { createThumbnailsProvider(get(named(PLAYER_THUMBNAIL_DIR))) }
    single { createImageLoader(get()) }

    single { createVlcLibManager(androidContext()) }
    single { createVlcPlayerController(get()) }
    single { createVlcPlayerListManager(get(), get(), get()) }
}

/**
 * Player工作目录
 */
private fun createPlayerDataDir(context: Context): File {
    return context.getExternalFilesDir(null)!!
}

/**
 * 视频缩略图存放目录
 */
private fun createPlayerThumbnailDir(dataDir: File): File {
    return File(dataDir, Medialibrary.MEDIALIB_FOLDER_NAME)
}

/**
 * 视频缩略图生成工具
 */
private fun createThumbnailsProvider(cacheDir: File): ThumbnailsProvider {
    return ThumbnailsProvider(cacheDir)
}

/**
 * 图片加载工具
 */
private fun createImageLoader(provider: ThumbnailsProvider): ImageLoader {
    return ImageLoader(provider)
}

/**
 * 弹幕引擎
 */
private fun createDanmakuEngine(): IDanmakuEngine {
    return DanmakuEngine(DanmaOptions.createOptions())
}

/**
 * VlcLib管理工具
 */
private fun createVlcLibManager(context: Context): VlcLibManager {
    return VlcLibManager(context)
}

/**
 * Vlc播放器控制
 */
private fun createVlcPlayerController(instance: VlcLibManager): VlcPlayerController {
    return VlcPlayerController(instance)
}

/**
 * Vlc列表管理
 */
private fun createVlcPlayerListManager(
    player: VlcPlayerController,
    historyRepo: VideoHistoryRepository,
    prefDataSource: PrefDataSource
): VlcPlayerListManager {
    return VlcPlayerListManager(player, historyRepo, prefDataSource)
}