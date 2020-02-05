package com.seiko.player.media.creator

import android.content.Context
import com.seiko.player.media.ijkplayer.MediaPlayerCreator
import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer
import tv.danmaku.ijk.media.player.AndroidMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer

class ExoMediaPlayerCreator(private val context: Context) : MediaPlayerCreator {
    override fun createPlayer(): IMediaPlayer {
        return IjkExoMediaPlayer(context)
    }
}