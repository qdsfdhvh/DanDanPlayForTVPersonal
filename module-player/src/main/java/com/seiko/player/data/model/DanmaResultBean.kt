package com.seiko.player.data.model

data class DanmaResultBean(
    // 弹幕评论集合
    val comments: List<DanmaCommentBean>,
    // 弹幕对于当前视频的偏移时间
    val shift: Long
)