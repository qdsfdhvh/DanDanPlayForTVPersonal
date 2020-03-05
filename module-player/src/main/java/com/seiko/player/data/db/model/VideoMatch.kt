package com.seiko.player.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "VideoMatch_table",
    indices = [
        Index(value = ["videoMd5"], unique = false)
    ]
)
data class VideoMatch(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Long = 0,

    // 视频md5
    var videoMd5: String = "",

    // 动漫id
    var animeId: Long = -1,

    // 集数id
    var episodeId: Int = -1,

    // 准确匹配
    var isMatched: Boolean = false,

    // 弹幕偏移时间
    var shift: Long = 0
)