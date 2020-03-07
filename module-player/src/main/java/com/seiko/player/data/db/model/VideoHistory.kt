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
data class VideoHistory(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Long = 0,

    // 视频md5
    var videoMd5: String = "",

    // 视频路径
    var videoPath: String = "",
    // 视频标题
    var videoTitle: String = "",
    // 视频缩略图
    var videoThumbnail: String = "",

    // 视频大小
    var videoSize: Long = 0,
    // 视频长度
    var videoDuration: Long = 0,
    // 视频播放进度
    var videoCurrentPosition: Long = 0,

    // 更新时间
    var updateTime: Long = 0,
    // 添加时间
    var createTime: Long = 0
)