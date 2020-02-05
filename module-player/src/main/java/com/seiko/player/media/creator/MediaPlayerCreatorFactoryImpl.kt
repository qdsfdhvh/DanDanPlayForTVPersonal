package com.seiko.player.media.creator

import com.seiko.player.media.ijkplayer.MediaPlayerCreator
import org.koin.core.KoinComponent
import org.koin.core.inject

class MediaPlayerCreatorFactoryImpl : MediaPlayerCreatorFactory, KoinComponent {

    private val ijkMediaPlayerCreator: IjkMediaPlayerCreator by inject()
    private val androidMediaPlayerCreator: AndroidMediaPlayerCreator by inject()
    private val exoMediaPlayerCreator: ExoMediaPlayerCreator by inject()

    override fun getCreator(@MediaPlayerCreatorFactory.Type type: Int): MediaPlayerCreator {
        return when(type) {
            MediaPlayerCreatorFactory.Type.IJK_PLAYER -> ijkMediaPlayerCreator
            MediaPlayerCreatorFactory.Type.EXO_PLAYER -> exoMediaPlayerCreator
            else ->androidMediaPlayerCreator
        }
    }
}