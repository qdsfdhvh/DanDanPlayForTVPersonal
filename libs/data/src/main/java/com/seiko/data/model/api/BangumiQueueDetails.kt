package com.seiko.data.model.api

//BangumiQueueDetailsV2 {
//    animeId (integer): 作品编号 ,
//    animeTitle (string, optional): 作品标题 ,
//    isOnAir (boolean): 是否正在连载中 ,
//    imageUrl (string, optional): 海报图片地址 ,
//    searchKeyword (string, optional): 搜索资源的关键词 ,
//    lastWatched (string): 上次观看时间（null表示尚未看过） ,
//    episodes (Array[BangumiQueueEpisodeV2], optional): 未看剧集的列表
//}
data class BangumiQueueDetails(
    val animeId: Int,
    val aimeTitle: String,
    val isOnAir: Boolean,
    val imageUrl: String,
    val searchKeyword: String,
    val lastWatched: String,
    val episodes: List<BangumiQueueEpisode>
)