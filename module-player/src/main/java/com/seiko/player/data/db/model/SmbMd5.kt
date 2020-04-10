package com.seiko.player.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SmbMd5_table",
    indices = [
        Index(value = ["uri"], unique = true)
    ]
)
data class SmbMd5(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Long = 0,

    // smb视频连接
    var uri: String = "",

    // 此资源的MD5
    var videoMd5: String = ""
)