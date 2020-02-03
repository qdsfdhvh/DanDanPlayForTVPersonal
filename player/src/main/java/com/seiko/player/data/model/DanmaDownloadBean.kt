package com.seiko.player.data.model

data class DanmaDownloadBean(
    var count: Int = 0,
    var comments: List<CommentsBean> = emptyList()
)