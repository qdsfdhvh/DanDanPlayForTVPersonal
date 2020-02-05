package com.seiko.player.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.seiko.player.data.model.DanmaDownloadBean

@Entity(
    tableName = "Danma_table",
    indices = [
        Index(value = ["episodeId"], unique = true)
    ]
)
data class Danma(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Long = 0,

    // 视频地址
    var videoPath: String = "",

    // 种子hash
    var hash: String = "",

    // 集数id
    var episodeId: Int = -1,

    // 弹幕数据
    var danma: DanmaDownloadBean = DanmaDownloadBean(),

    // 下载时间
    var downloadDate: Long = 0
)