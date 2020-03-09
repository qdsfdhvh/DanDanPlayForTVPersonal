package com.seiko.player.data.api.model

import com.seiko.player.data.model.DanmaCommentBean
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DanmaDownloadResponse @JvmOverloads constructor(
    var count: Int = 0,
    var comments: List<DanmaCommentBean> = emptyList()
) {
    companion object {
        fun empty() = DanmaDownloadResponse()
    }
}