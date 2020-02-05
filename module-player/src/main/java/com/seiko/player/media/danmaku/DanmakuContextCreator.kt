package com.seiko.player.media.danmaku

import master.flame.danmaku.danmaku.model.android.DanmakuContext

interface DanmakuContextCreator {
    fun create(): DanmakuContext
}