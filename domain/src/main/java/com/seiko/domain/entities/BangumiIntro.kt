package com.seiko.domain.entities

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
data class BangumiIntro(
    val airDay: Int,
    val animeId: Int,
    val animeTitle: String,
    val imageUrl: String,
    val isFavorited: Boolean,
    val isOnAir: Boolean,
    val isRestricted: Boolean,
    val rating: Int,
    val searchKeyword: String
)