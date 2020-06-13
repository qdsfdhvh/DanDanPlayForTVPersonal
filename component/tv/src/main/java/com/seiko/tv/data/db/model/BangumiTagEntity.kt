package com.seiko.tv.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//BangumiTag {
//    id (integer): 标签编号 ,
//    name (string, optional): 标签内容
//}
@Entity(
    tableName = "BangumiTag",
    indices = [
        Index(value = ["fromAnimeId", "id"], unique = false)
    ]
)
@JsonClass(generateAdapter = true)
data class BangumiTagEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    @field:Json(name = "_id")
    var id: Long = 0,

    var fromAnimeId: Long = 0,

    @ColumnInfo(name = "id")
    @field:Json(name = "id")
    var tagId: Int = 0,

    @ColumnInfo(name = "name")
    @field:Json(name = "name")
    var tagName: String = ""
)