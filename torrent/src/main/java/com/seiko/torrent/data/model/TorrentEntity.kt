package com.seiko.torrent.data.model

import androidx.room.*
import org.libtorrent4j.Priority

@Entity(
    tableName = "Torrent",
    indices = [
        Index(value = ["hash"], unique = true)  // 创建索引，键值唯一
    ]
)
data class TorrentEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Long = 0,

    var hash: String = "",
    var source: String = "",
    var downloadPath: String = "",

    var name: String = "",
    var priorityList: List<Priority>? = null, // 下载种子里的哪些文件

    var sequentialDownload: Boolean = false,
    var paused: Boolean = false,
    var finished: Boolean = false,
    var downloadingMetadata: Boolean = false,

    var addedDate: Long = 0,
    var error: String = ""
)