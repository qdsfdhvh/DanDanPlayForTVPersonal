package com.seiko.player.media.ijkplayer

import tv.danmaku.ijk.media.player.IMediaPlayer

interface MediaPlayerCreator {
    fun createPlayer(): IMediaPlayer
}