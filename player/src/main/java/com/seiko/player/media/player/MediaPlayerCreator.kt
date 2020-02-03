package com.seiko.player.media.player

import tv.danmaku.ijk.media.player.IMediaPlayer

interface MediaPlayerCreator {
    fun createPlayer(): IMediaPlayer
}