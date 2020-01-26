package com.dandanplay.tv.data.db.model

import androidx.room.*
import java.io.Serializable

//BangumiEpisode {
//    episodeId (integer): 剧集ID（弹幕库编号） ,
//    episodeTitle (string, optional): 剧集标题 ,
//    lastWatched (string): 上次观看时间（服务器时间，即北京时间） ,
//    airDate (string): 本集上映时间（当地时间）
//}
@Entity(
    tableName = "BangumiEpisode",
    indices = [
        Index(value = ["fromAnimeId"], unique = false)
    ]
)
data class BangumiEpisodeEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Long = 0,

    var fromAnimeId: Long = 0,

    var episodeId: Int = 0,
    var episodeTitle: String = "",

    var airDate: String = "",
    @Ignore
    var lastWatched: String = "",
    @Ignore
    var isDownloaded: Boolean = false
) : Serializable