package com.seiko.core.model.api

//BangumiQueueIntroV2 {
//    animeId (integer): 作品编号 ,
//    animeTitle (string, optional): 作品标题 ,
//    episodeTitle (string, optional): 最新一集的剧集标题 ,
//    airDate (string): 剧集上映日期（无小时分钟，当地时间） ,
//    imageUrl (string, optional): 海报图片地址 ,
//    description (string, optional): 未看状态的说明，如“今天更新”，“昨天更新”，“有多集未看”等 ,
//    isOnAir (boolean): 番剧是否在连载中
//}
data class BangumiQueueIntro(
    val animeId: Int,
    val animeTitle: String,
    val episodeTitle: String,
    val airDate: String,
    val imageUrl: String,
    val description: String,
    val isOnAir: Boolean
)