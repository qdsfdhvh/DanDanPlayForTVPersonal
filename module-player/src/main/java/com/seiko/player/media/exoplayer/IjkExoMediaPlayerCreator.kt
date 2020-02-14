package com.seiko.player.media.exoplayer

import android.content.Context
import com.seiko.player.media.exoplayer.ExoSourceManager
import com.seiko.player.media.exoplayer.IjkExoMediaPlayer
import com.seiko.player.media.ijkplayer.MediaPlayerCreator
import okhttp3.OkHttpClient
import tv.danmaku.ijk.media.player.IMediaPlayer

class IjkExoMediaPlayerCreator(
    private val context: Context,
    private val okHttpClient: OkHttpClient
) : MediaPlayerCreator {
    override fun createPlayer(): IMediaPlayer {
        val exoSourceManager = ExoSourceManager.newInstance(context, okHttpClient)
        return IjkExoMediaPlayer(context, exoSourceManager)
    }
}