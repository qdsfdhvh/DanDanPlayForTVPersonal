package com.seiko.player.service

import android.view.View
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.RendererItem
import org.videolan.libvlc.util.DisplayManager

interface IPlayerController {

    /**
     * 播放器
     */
//    fun getMediaPlayer(): MediaPlayer

    fun attachView(surfaceFrame: View, displayManager: DisplayManager?)

    fun detachView()

    fun getRendererItem(): RendererItem?

    /**
     * 调整播放速度
     * @param rate 速度 默认1.0
     * @param save 是否保存进度
     */
    fun setRate(rate: Float, save: Boolean)

    /**
     * 播放
     */
    fun play(): Boolean

    /**
     * 暂停
     */
    fun pause(): Boolean

    /**
     * 停止
     */
    fun stop(): Boolean

    /**
     * 释放播放器
     */
    fun release()

    fun restart()
}