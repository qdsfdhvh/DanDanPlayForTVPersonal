package com.seiko.player.data.model

import com.seiko.danma.model.Danma
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DanmaCommentBean(
    var cid: Int = 0,
    var p: String = "",
    var m: String = ""
) : Danma {
    override fun cid(): Int {
        return cid
    }

    override fun p(): String {
        return p
    }

    override fun m(): String {
        return m
    }
}