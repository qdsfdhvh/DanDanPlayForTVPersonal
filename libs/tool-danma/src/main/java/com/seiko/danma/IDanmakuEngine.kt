package com.seiko.danma

import com.seiko.danma.model.Danma
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.controller.IDanmakuView
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import tv.danmaku.ijk.media.player.IMediaPlayer

interface IDanmakuEngine {

    /**
     * 绑定播放器
     * @param danmaView 弹幕视图
     */
    fun bindDanmakuView(danmaView: IDanmakuView)

    /**
     * 弹幕集合
     */
    fun setDanmaList(danma: List<Danma>)

    /**
     * 开始
     */
    fun play()

    /**
     * 停止
     */
    fun pause()

    /**
     * 注销
     */
    fun release()

    /**
     * 显示弹幕
     */
    fun show()

    /**
     * 影藏弹幕
     */
    fun hide()

    fun seekTo(position: Long)

    fun setCallback(callback: DrawHandler.Callback?)

}