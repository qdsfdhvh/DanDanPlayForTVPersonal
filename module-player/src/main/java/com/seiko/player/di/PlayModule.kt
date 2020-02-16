package com.seiko.player.di

import android.content.Context
import com.seiko.danma.DanmakuEngine
import com.seiko.danma.IDanmakuEngine
import com.seiko.player.data.prefs.PrefDataSource
import com.seiko.player.media.PlayerOptions
import com.seiko.player.media.creator.*
import com.seiko.player.media.exoplayer.IjkExoMediaPlayerCreator
import com.seiko.player.media.ijkplayer.IjkMediaPlayerCreator
import com.seiko.subtitle.ISubtitleEngine
import com.seiko.subtitle.SubtitleEngine
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val playModule = module {
    single { createIjkMediaPlayerCreator(get()) }
    single { createExoMediaPlayerCreator(androidContext(), get()) }
    single { createMediaPlayerFactory() }
    single { createDanmakuEngine() }
    factory { createSubtitleEngine() }
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