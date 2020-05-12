package com.seiko.tv.data.model

import com.seiko.tv.data.db.model.BangumiEpisodeEntity

class BangumiDetailBean(
    val animeTitle: String,
    val imageUrl: String,
    val tags: String,
    val description: String,
    val rating: Float,
    val isFavorited: Boolean,

    val titleColor: Int,
    val bodyColor: Int,
    val actionBackgroundColor: Int,
    val overviewRowBackgroundColor: Int
) {
    companion object {
        fun empty() = BangumiDetailBean(
            animeTitle = "",
            imageUrl = "",
            tags = "",
            description = "",
            rating = 0f,
            isFavorited = false,

            titleColor = 0,
            bodyColor = 0,
            actionBackgroundColor = 0,
            overviewRowBackgroundColor = 0
        )
    }
}