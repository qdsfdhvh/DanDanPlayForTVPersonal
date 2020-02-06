package com.seiko.player.media.creator

import android.content.Context
import com.seiko.player.media.exoplayer.ExoSourceManager
import com.seiko.player.media.exoplayer.IjkExoMediaPlayer
import com.seiko.player.media.ijkplayer.MediaPlayerCreator
import okhttp3.OkHttpClient
import tv.danmaku.ijk.media.player.IMediaPlayer

class ExoMediaPlayerCreator(
    private val context: Context,
    private val okHttpClient: OkHttpClient
) : MediaPlayerCreator {
    override fun createPlayer(): IMediaPlayer {
        val exoSourceManager = ExoSourceManager.newInstance(context, okHttpClient)
        return IjkExoMediaPlayer(context, exoSourceManager)
    }
}