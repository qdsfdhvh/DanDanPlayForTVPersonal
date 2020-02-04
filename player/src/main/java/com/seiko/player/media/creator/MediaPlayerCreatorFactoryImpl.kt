package com.seiko.player.media.creator

import org.koin.core.KoinComponent
import org.koin.core.inject

class MediaPlayerCreatorFactoryImpl : MediaPlayerCreatorFactory, KoinComponent {

    private val ijkMediaPlayerCreator: IjkMediaPlayerCreator by inject()
    private val androidMediaPlayerCreator: AndroidMediaPlayerCreator by inject()

    override fun getCreator(@MediaPlayerCreatorFactory.Type type: Int): MediaPlayerCreator {
        return when(type) {
            MediaPlayerCreatorFactory.Type.IJK_PLAYER -> ijkMediaPlayerCreator
            else ->androidMediaPlayerCreator
        }
    }
}