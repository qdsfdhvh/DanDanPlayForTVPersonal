package com.seiko.player.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "VideoMedia_table",
    indices = [
        Index(value = ["videoMd5"], unique = true)
    ]
)
data class VideoMedia(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "_id")
    var id: Long = 0,

    var videoMd5: String = "",

    var videoPath: String = "",
    var videoTitle: String = "",
    var videoThumbnail: String = "",

    var videoSize: Long = 0,
    var videoDuration: Long = 0,
    var videoCurrentPosition: Long = 0
)