package com.seiko.tv.data.model.api

import com.squareup.moshi.JsonClass

//BangumiQueueEpisodeV2 {
//    episodeId (integer): 剧集编号（弹幕库编号） ,
//    episodeTitle (string, optional): 剧集标题 ,
//    airDate (string): 上映日期（无小时分钟，当地时间），可能为null
//}
@JsonClass(generateAdapter = true)
data class BangumiQueueEpisode(
    val episodeId: Int,
    val episodeTitle: String,
    val airDate: String
)