package com.seiko.player.di

import com.seiko.player.data.prefs.PrefDataSource
import com.seiko.player.media.creator.AndroidMediaPlayerCreator
import com.seiko.player.media.creator.IjkMediaPlayerCreator
import com.seiko.player.media.creator.MediaPlayerCreatorFactory
import com.seiko.player.media.creator.MediaPlayerCreatorFactoryImpl
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
