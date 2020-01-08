package com.seiko.domain.model.api


//SearchAnimeDetails {
//    animeId (integer): 作品ID ,
//    animeTitle (string, optional): 作品标题 ,
//    type (string): 作品类型 = ['tvseries', 'tvspecial', 'ova', 'movie', 'musicvideo', 'web', 'other', 'jpmovie', 'jpdrama', 'unknown'],
//    typeDescription (string, optional): 类型描述 ,
//    imageUrl (string, optional): 海报图片地址 ,
//    startDate (string): 上映日期 ,
//    episodeCount (integer): 剧集总数 ,
//    rating (number): 此作品的综合评分（0-10） ,
//    isFavorited (boolean): 当前用户是否已关注此作品
//}
data class SearchAnimeDetails(
    var animeId: Int = 0,
    var animeTitle: String = "",
    var episodeCount: Int = 0,
    var imageUrl: String = "",
    var isFavorited: Boolean = false,
    var rating: Int = 0,
    var startDate: String = "",
    var type: String = "",
    var typeDescription: String = ""
)