package com.dandanplay.tv.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// {
// "Title":"【傲娇零&自由字幕组】[刀剑神域III UnderWorld/Sword Art Online - Alicization][01][HEVC-10Bit-1080P AAC][外挂GB/BIG5][WEB-Rip][MP4+ass]",
// "TypeId":2,
// "TypeName":"动画/新番连载",
// "SubgroupId":532,
// "SubgroupName":"傲娇零字幕组",
// "Magnet":"magnet:?xt=urn:btih:WEORDPJIJANN54BH2GNNJ6CSN7KB7S34",
// "PageUrl":"https://share.dmhy.org/topics/view/501340_III_UnderWorld_Sword_Art_Online_-_Alicization_01_HEVC-10Bit-2160P_AAC_GB_BIG5_WEB-Rip_MP4_ass.html",
// "FileSize":"818.7MB",
// "PublishDate":"2018-10-12 12:44:00"
@Entity(
    tableName = "ResMagnetItem",
    indices = [
        Index(value = ["hash"], unique = true) // hash应该唯一
    ]
)

data class ResMagnetItemEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Long = 0,
    var hash: String = "", // 添加此字段，方便表管理

    @SerializedName("Title")
    var title: String = "",
    @SerializedName("TypeId")
    var typeId: Int = 0,
    @SerializedName("TypeName")
    var typeName: String = "",
    @SerializedName("SubgroupId")
    var subgroupId: Int = 0,
    @SerializedName("SubgroupName")
    var subgroupName: String = "",
    @SerializedName("Magnet")
    var magnet: String = "",
    @SerializedName("PageUrl")
    var pageUrl: String = "",
    @SerializedName("FileSize")
    var fileSize: String = "",
    @SerializedName("PublishDate")
    var publishDate: String = "",

    var addedDate: Long = 0
)