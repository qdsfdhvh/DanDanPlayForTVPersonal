package com.seiko.player.di

import com.seiko.player.data.prefs.PrefDataSource
import com.seiko.player.media.player.AndroidMediaPlayerCreator
import com.seiko.player.media.player.IjkMediaPlayerCreator
import com.seiko.player.media.player.MediaPlayerCreatorFactory
import com.seiko.player.media.player.MediaPlayerCreatorFactoryImpl
import org.koin.dsl.module

val playModule = module {
    single { createIjkMediaPlayerCreator(get()) }
    single { createAndroidMediaPlayerCreator() }
    single { createMediaPlayerFactory() }
}

private fun createIjkMediaPlayerCreator(prefs: PrefDataSource): IjkMediaPlayerCreator {
    return IjkMediaPlayerCreator(prefs)
}

private fun createAndroidMediaPlayerCreator(): AndroidMediaPlayerCreator {
    return AndroidMediaPlayerCreator()
}

private fun createMediaPlayerFactory(): MediaPlayerCreatorFactory {
    return MediaPlayerCreatorFactoryImpl()
}
