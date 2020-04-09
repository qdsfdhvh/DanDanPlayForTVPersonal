package com.seiko.danma

import com.seiko.danma.model.Danma
import com.seiko.danma.util.log
import master.flame.danmaku.controller.DimensionTimer
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.controller.IDanmakuView
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import kotlin.math.max

class DanmakuEngine(
    private val config: DanmakuEngineOptions
) : IDanmakuEngine, DrawHandler.Callback {

    private var danmaView: IDanmakuView? = null
    private var danmaCallback: DrawHandler.Callback? = null

    private var danmaParser: BaseDanmakuParser? = null
    private var danmaContext: DanmakuContext? = null
    private var showDanma = true

    /**
     * 弹幕偏移时间
     */
    private var shift = 0L

    /**
     * 填充弹幕
     */
    private fun prepareDanma() {
        val parser = danmaParser ?: return
        danmaContext = config.getDanmaConfig()
        danmaView?.prepare(parser, danmaContext)
    }

    /**
     * 显示/隐藏弹幕
     */
    private fun setDanmaShow() {
        danmaView?.run {
            if (showDanma) show() else hide()
        }
    }

    override fun bindDanmakuView(danmaView: IDanmakuView) {
        log("bindToMediaPlayer")
        this.danmaView = danmaView
        danmaView.setDrawingThreadType(config.drawType)
        danmaView.showFPS(config.showFps)
        danmaView.enableDanmakuDrawingCache(config.drawingCache)
        danmaView.setCallback(this)
        prepareDanma()
        setDanmaShow()
    }

    override fun setDanmaList(danma: List<Danma>, shift: Long) {
        danmaParser = JsonDanmakuParser(danma)
        this.shift = shift
        prepareDanma()
    }

    override fun play() {
        val danmaView = danmaView ?: return
        if (danmaView.isPrepared && danmaView.isPaused) {
            danmaView.resume()
        }
    }

    override fun pause() {
        val danmaView = danmaView ?: return
        if (danmaView.isPrepared && !danmaView.isPaused) {
            danmaView.pause()
        }
    }

    override fun release() {
        shift = 0
        DimensionTimer.getInstance().setTimeRate(1.0f)
        danmaParser = null
        danmaView?.release()
        danmaView = null
        danmaCallback = null
    }

    override fun show() {
        if (!showDanma) {
            showDanma = true
            setDanmaShow()
        }
    }

    override fun hide() {
        if (showDanma) {
            showDanma = false
            setDanmaShow()
        }
    }

    override fun setRate(rate: Float) {
        DimensionTimer.getInstance().setTimeRate(max(0f, rate))
    }

    override fun seekTo(position: Long) {
        danmaView?.seekTo(position + shift)
    }

    override fun drawingFinished() {
        danmaCallback?.drawingFinished()
    }

    override fun danmakuShown(danmaku: BaseDanmaku?) {
        danmaCallback?.danmakuShown(danmaku)
    }

    override fun prepared() {
        log("prepared")
        danmaCallback?.prepared()
    }

    override fun updateTimer(timer: DanmakuTimer) {
        danmaCallback?.updateTimer(timer)
    }

    override fun setCallback(callback: DrawHandler.Callback?) {
        danmaCallback = callback
    }
}