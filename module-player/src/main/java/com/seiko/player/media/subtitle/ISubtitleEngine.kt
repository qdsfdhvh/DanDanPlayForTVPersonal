package com.seiko.player.media.subtitle

import com.seiko.player.media.subtitle.model.Subtitle
import tv.danmaku.ijk.media.player.IMediaPlayer

interface ISubtitleEngine {

    fun bindToMediaPlayer(mediaPlayer: IMediaPlayer?)

    fun setSubtitlePath(path: String)

    fun start()

    fun stop()

    fun release()

    fun setOnSubtitleListener(listener: OnSubtitleListener?)

    interface OnSubtitleListener {
        fun onSubtitleChanged(subtitle: Subtitle?)
        fun onSubtitlePrepared()
    }

}