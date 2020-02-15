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
    val overviewRowBackgroundColor: Int,

    val episodes: List<BangumiEpisodeEntity>,
    val relateds: List<HomeImageBean>,
    val similars: List<HomeImageBean>
)