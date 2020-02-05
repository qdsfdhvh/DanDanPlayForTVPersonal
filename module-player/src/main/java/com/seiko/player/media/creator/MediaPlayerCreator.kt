package com.seiko.player.media.creator

import tv.danmaku.ijk.media.player.IMediaPlayer

interface MediaPlayerCreator {
    fun createPlayer(): IMediaPlayer
}