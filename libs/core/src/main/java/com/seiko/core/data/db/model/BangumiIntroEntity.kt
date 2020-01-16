package com.seiko.core.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.seiko.core.constants.BangumiIntroType
import java.io.Serializable

//BangumiIntro {
//    animeId (integer): 作品编号 ,
//    animeTitle (string, optional): 作品标题 ,
//    imageUrl (string, optional): 海报图片地址 ,
//    searchKeyword (string, optional): 搜索关键词 ,
//    isOnAir (boolean): 是否正在连载中 ,
//    airDay (integer): 周几上映，0代表周日，1-6代表周一至周六 ,
//    isFavorited (boolean): 当前用户是否已关注（无论是否为已弃番等附加状态） ,
//    isRestricted (boolean): 是否为限制级别的内容（例如属于R18分级） ,
//    rating (number): 番剧综合评分（综合多个来源的评分求出的加权平均值，0-10分）
//}

@Entity(
    tableName = "BangumiIntro",
    indices = [
        Index(value = ["fromAnimeId", "fromType"], unique = false)
    ]
)
data class BangumiIntroEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Long = 0,

    var fromAnimeId: Long = 0,

    @BangumiIntroType
    var fromType: Int = 0,

    var animeId: Long = 0,
    var animeTitle: String = "",
    var imageUrl: String = "",

    var isFavorited: Boolean = false,
    var isOnAir: Boolean = false,
    var isRestricted: Boolean = false,
    var airDay: Int = 0,
    var rating: Int = 0,
    var searchKeyword: String = ""
) : Serializable {
    override fun toString(): String {
        return "BangumiIntro{" +
                "airDay=$airDay," +
                "animeId=$animeId," +
                "animeTitle=$animeTitle," +
                "imageUrl=$imageUrl" +
                "isFavorited=$isFavorited," +
                "isOnAir=$isOnAir," +
                "isRestricted=$isRestricted," +
                "rating=$rating," +
                "searchKeyword=$searchKeyword" +
                "}"
    }
}