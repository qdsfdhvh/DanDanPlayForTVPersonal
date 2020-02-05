package com.seiko.player.media.danmaku

import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.DanmakuContext

class DefaultDanmakuContextCreator : DanmakuContextCreator {

    override fun create(): DanmakuContext {
        val danmakuContext = DanmakuContext.create()
        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 2F)
            //设置防弹幕重叠
//            .preventOverlapping()
            //合并重复弹幕
//            .setDuplicateMergingEnabled(true)
            //弹幕滚动速度
            .setScrollSpeedFactor(1.2f)
            //弹幕文字大小
            .setScaleTextSize(1.8f)
            //弹幕文字透明度
            .setDanmakuTransparency(0.8f)
            //是否显示滚动弹幕
            .setR2LDanmakuVisibility(true)
            //是否显示顶部弹幕
            .setFTDanmakuVisibility(true)
            //是否显示底部弹幕
            .setFBDanmakuVisibility(true)
            //同屏数量限制
            .setMaximumVisibleSizeInScreen(1000)
        return danmakuContext
    }

}