package com.seiko.player.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.seiko.player.data.api.model.DanmaDownloadResponse
import com.seiko.player.data.model.DanmaCommentBean

@Entity(
    tableName = "Danma_table",
    indices = [
        Index(value = ["videoMd5"], unique = true)
    ]
)
data class VideoDanmaku(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Long = 0,

    // 视频md5
    var videoMd5: String = "",

    // 弹幕数据
    var danma: List<DanmaCommentBean> = emptyList(),

    // 下载时间
    var downloadDate: Long = 0
)