package com.seiko.data.model.db

import androidx.room.*
import org.libtorrent4j.Priority

@Entity(
    tableName = "Torrent",
    indices = [
        Index(value = ["HASH"], unique = true)  // 创建索引，键值唯一
    ]
)
data class TorrentEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Long = 0,

    @ColumnInfo(name = "HASH")
    var hash: String = "",
    @ColumnInfo(name = "SOURCE")
    var source: String = "",
    @ColumnInfo(name = "DOWNLOAD_PATH")
    var downloadPath: String = "",

    @ColumnInfo(name = "NAME")
    var name: String = "",
    @ColumnInfo(name = "PRIORITY_LIST")
    var priorityList: List<Priority>? = null, // 下载种子里的哪些文件

    @ColumnInfo(name = "SEQUENTIAL_DOWNLOAD")
    var sequentialDownload: Boolean = false,
    @ColumnInfo(name = "PAUSED")
    var paused: Boolean = false,
    @ColumnInfo(name = "FINISHED")
    var finished: Boolean = false,
    @ColumnInfo(name = "DOWNLOADING_METADATA")
    var downloadingMetadata: Boolean = false,

    @ColumnInfo(name = "ADDED_DATE")
    var addedDate: Long = 0,
    @ColumnInfo(name = "ERROR")
    var error: String = ""

)