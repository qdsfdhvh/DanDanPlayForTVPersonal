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
import com.seiko.subtitle.ISubtitleEngine
import com.seiko.subtitle.SubtitleEngine
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

val playModule = module {
    single(named(PLAYER_DATA_DIR)) { createPlayerDataDir(androidContext()) }
    single(named(PLAYER_THUMBNAIL_DIR)) { createPlayerThumbnailDir(get(named(PLAYER_DATA_DIR))) }
    single { createIjkMediaPlayerCreator(get()) }
    single { createExoMediaPlayerCreator(androidContext(), get()) }
    single { createMediaPlayerFactory() }
    single { createDanmakuEngine() }
    factory { createSubtitleEngine() }
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
    return File(dataDir, "videoThumbnail")
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