package com.dandanplay.tv.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

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
data class BangumiTagEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    @SerializedName(value = "_id")
    var id: Long = 0,

    var fromAnimeId: Long = 0,

    @ColumnInfo(name = "id")
    @SerializedName(value = "id")
    var tagId: Int = 0,

    @ColumnInfo(name = "name")
    @SerializedName(value = "name")
    var tagName: String = ""
)