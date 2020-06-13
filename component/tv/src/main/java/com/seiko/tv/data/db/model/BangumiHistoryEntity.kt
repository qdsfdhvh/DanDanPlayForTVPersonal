package com.seiko.tv.data.db.model

import androidx.room.*
import com.squareup.moshi.JsonClass

@Entity(
    tableName = "BangumiHistory"
)
@JsonClass(generateAdapter = true)
data class BangumiHistoryEntity(
    @PrimaryKey(autoGenerate = false)
    var animeId: Long = 0,
    var animeTitle: String = "",
    var imageUrl: String = "",

    var type: String = "",
    var typeDescription: String = "",
    var summary: String = "",
    var bangumiUrl: String = "",

    var isOnAir: Boolean = false,
    var airDay: Int = 0,
    var searchKeyword: String = "",
    var isRestricted: Boolean = false,
    var rating: Float = 0f,

    var updateDate: Long = 0,
    var createDate: Long = 0
)