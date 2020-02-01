package com.seiko.player.media

import android.view.View
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.RendererItem
import org.videolan.libvlc.util.DisplayManager

interface IPlayerController {

    /**
     * 播放器
     */
    fun attachView(surfaceFrame: View, displayManager: DisplayManager?)

    fun detachView()

    /**
     * 是否有播放源
     */
    fun hasMedia(): Boolean

    /**
     * 是否已经注销
     */
    fun isReleased(): Boolean

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

}