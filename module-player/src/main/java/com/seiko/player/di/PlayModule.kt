package com.seiko.player.di

import android.content.Context
import com.seiko.danma.DanmakuEngine
import com.seiko.danma.IDanmakuEngine
import com.seiko.player.data.prefs.PrefDataSource
import com.seiko.player.media.PlayerOptions
import com.seiko.player.media.creator.*
import com.seiko.player.media.exoplayer.IjkExoMediaPlayerCreator
import com.seiko.player.media.ijkplayer.IjkMediaPlayerCreator
import com.seiko.player.util.constants.PLAYER_DATA_DIR
import com.seiko.player.util.constants.PLAYER_THUMBNAIL_DIR
import com.seiko.player.util.bitmap.ImageLoader
import com.seiko.player.util.bitmap.ThumbnailsProvider
import com.seiko.player.vlc.media.PlayerController
import com.seiko.player.vlc.media.PlayerManager
import com.seiko.player.vlc.media.VlcInstance
import com.seiko.player.vlc.media.VlcOptions
import com.seiko.subtitle.ISubtitleEngine
import com.seiko.subtitle.SubtitleEngine
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.videolan.medialibrary.interfaces.Medialibrary
import java.io.File

val playModule = module {
    single(named(PLAYER_DATA_DIR)) { createPlayerDataDir(androidContext()) }
    single(named(PLAYER_THUMBNAIL_DIR)) { createPlayerThumbnailDir(get(named(PLAYER_DATA_DIR))) }

    single { createIjkMediaPlayerCreator(get()) }
    single { createExoMediaPlayerCreator(androidContext(), get()) }
    single { createMediaPlayerFactory() }

    single { createDanmakuEngine() }
    factory { createSubtitleEngine() }

    single { createThumbnailsProvider(get(named(PLAYER_THUMBNAIL_DIR))) }
    single { createImageLoader(get()) }

    single { createVlcOptions(androidContext()) }
    single { createVlcInstance(androidContext(), get()) }
    single { createPlayerController(get()) }
    single { createPlayerListManager(get(), get()) }
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

private fun createIjkMediaPlayerCreator(prefs: PrefDataSource): IjkMediaPlayerCreator {
    return IjkMediaPlayerCreator(prefs)
}

private fun createExoMediaPlayerCreator(context: Context, okHttpClient: OkHttpClient): IjkExoMediaPlayerCreator {
    return IjkExoMediaPlayerCreator(context, okHttpClient)
}

private fun createMediaPlayerFactory(): MediaPlayerCreatorFactory {
    return MediaPlayerCreatorFactoryImpl()
}

private fun createDanmakuEngine(): IDanmakuEngine {
    return DanmakuEngine(PlayerOptions.createDanmakuOptions())
}

private fun createSubtitleEngine(): ISubtitleEngine {
    return SubtitleEngine()
}

private fun createThumbnailsProvider(cacheDir: File): ThumbnailsProvider {
    return ThumbnailsProvider(cacheDir)
}

private fun createImageLoader(provider: ThumbnailsProvider): ImageLoader {
    return ImageLoader(provider)
}

private fun createVlcOptions(context: Context): VlcOptions {
    return VlcOptions(context)
}

private fun createVlcInstance(context: Context, options: VlcOptions): VlcInstance {
    return VlcInstance(context, options)
}

private fun createPlayerController(instance: VlcInstance): PlayerController {
    return PlayerController(instance)
}

private fun createPlayerListManager(instance: VlcInstance, player: PlayerController): PlayerManager {
    return PlayerManager(instance, player)
}