package com.seiko.player.di

import android.content.Context
import com.seiko.player.data.prefs.PrefDataSource
import com.seiko.player.media.creator.*
import com.seiko.player.media.danmaku.DanmakuContextCreator
import com.seiko.player.media.danmaku.DefaultDanmakuContextCreator
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val playModule = module {
    single { createIjkMediaPlayerCreator(get()) }
    single { createExoMediaPlayerCreator(androidContext(), get()) }
    single { createAndroidMediaPlayerCreator() }
    single { createMediaPlayerFactory() }
    single { createDanmakuContextCreator() }
}

private fun createIjkMediaPlayerCreator(prefs: PrefDataSource): IjkMediaPlayerCreator {
    return IjkMediaPlayerCreator(prefs)
}

private fun createExoMediaPlayerCreator(context: Context, okHttpClient: OkHttpClient): ExoMediaPlayerCreator {
    return ExoMediaPlayerCreator(context, okHttpClient)
}

private fun createAndroidMediaPlayerCreator(): AndroidMediaPlayerCreator {
    return AndroidMediaPlayerCreator()
}

private fun createMediaPlayerFactory(): MediaPlayerCreatorFactory {
    return MediaPlayerCreatorFactoryImpl()
}

private fun createDanmakuContextCreator(): DanmakuContextCreator {
    return DefaultDanmakuContextCreator()
}