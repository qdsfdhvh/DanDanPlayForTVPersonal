package com.seiko.player.media.player

import com.seiko.player.media.player.MediaPlayerCreator
import tv.danmaku.ijk.media.player.AndroidMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer

class AndroidMediaPlayerCreator :
    MediaPlayerCreator {
    override fun createPlayer(): IMediaPlayer {
        return AndroidMediaPlayer()
    }
}