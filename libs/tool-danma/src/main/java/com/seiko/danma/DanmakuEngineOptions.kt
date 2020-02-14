package com.seiko.danma

import master.flame.danmaku.controller.IDanmakuView
import master.flame.danmaku.danmaku.model.android.DanmakuContext

data class DanmakuEngineOptions(
    var drawType: Int = IDanmakuView.THREAD_TYPE_NORMAL_PRIORITY,
    var drawingCache: Boolean = true,
    var showFps: Boolean = false,
    var block: DanmakuContext.() -> Unit = {}
) {
    fun getDanmaConfig(): DanmakuContext {
        val danmaConfig = DanmakuContext.create()
        return danmaConfig.apply(block)
    }
}