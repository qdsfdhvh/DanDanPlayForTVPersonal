package com.seiko.player.service

import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.RendererItem

interface IPlayerController {

    /**
     * 播放器
     */
    fun getMediaPlayer(): MediaPlayer

    fun getRendererItem(): RendererItem?

    fun release()

    fun restart()
}