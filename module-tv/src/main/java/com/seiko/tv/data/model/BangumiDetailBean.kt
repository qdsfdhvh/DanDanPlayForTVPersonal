package com.seiko.tv.data.model

class BangumiDetailBean(
    val animeTitle: String,
    val imageUrl: String,
    val tags: String,
    val description: String,
    val rating: Float,
    val isFavorited: Boolean,
    var keyboard: String,

    val titleColor: Int,
    val bodyColor: Int,
    val actionBackgroundColor: Int,
    val overviewRowBackgroundColor: Int
) {
    companion object {
        fun empty(imageUrl: String = "") = BangumiDetailBean(
            animeTitle = "",
            imageUrl = imageUrl,
            tags = "",
            description = "",
            rating = 0f,
            isFavorited = false,
            keyboard = "",

            titleColor = 0,
            bodyColor = 0,
            actionBackgroundColor = 0,
            overviewRowBackgroundColor = 0
        )
    }
}