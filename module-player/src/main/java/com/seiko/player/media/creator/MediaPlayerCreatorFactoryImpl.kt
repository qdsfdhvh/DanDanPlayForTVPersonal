package com.seiko.player.media.creator

import com.seiko.player.media.exoplayer.IjkExoMediaPlayerCreator
import com.seiko.player.media.ijkplayer.IjkMediaPlayerCreator
import com.seiko.player.media.ijkplayer.MediaPlayerCreator
import org.koin.core.KoinComponent
import org.koin.core.inject
import tv.danmaku.ijk.media.player.AndroidMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer

class MediaPlayerCreatorFactoryImpl : MediaPlayerCreatorFactory, KoinComponent {

    private val ijkMediaPlayerCreator: IjkMediaPlayerCreator by inject()
    private val exoMediaPlayerCreator: IjkExoMediaPlayerCreator by inject()

    override fun getCreator(@MediaPlayerCreatorFactory.Type type: Int): MediaPlayerCreator {
        return when(type) {
            MediaPlayerCreatorFactory.Type.IJK_PLAYER -> ijkMediaPlayerCreator
            MediaPlayerCreatorFactory.Type.EXO_PLAYER -> exoMediaPlayerCreator
            else -> object : MediaPlayerCreator {
                override fun createPlayer(): IMediaPlayer {
                    return AndroidMediaPlayer()
                }
            }
        }
    }
}