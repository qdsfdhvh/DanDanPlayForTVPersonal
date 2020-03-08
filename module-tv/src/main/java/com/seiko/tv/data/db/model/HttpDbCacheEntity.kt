package com.seiko.tv.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "HttpDbCache_table",
    indices = [
        Index(value = ["key"], unique = true)
    ]
)
data class HttpDbCacheEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Long = 0,

    var key: String = "",
    var body: String = "",

    var updateTime: Long = 0,
    var createTime: Long = 0
)