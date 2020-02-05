package com.seiko.player.media.creator

import tv.danmaku.ijk.media.player.AndroidMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer

class AndroidMediaPlayerCreator :
    MediaPlayerCreator {
    override fun createPlayer(): IMediaPlayer {
        return AndroidMediaPlayer()
    }
}