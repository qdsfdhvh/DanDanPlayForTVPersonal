package com.seiko.player.data.model

import com.squareup.moshi.JsonClass

//MatchResultV2 {
//episodeId (integer): 弹幕库ID ,
//animeId (integer): 作品ID ,
//animeTitle (string, optional): 作品标题 ,
//episodeTitle (string, optional): 剧集标题 ,
//type (string): 作品类别 = ['tvseries', 'tvspecial', 'ova', 'movie', 'musicvideo', 'web', 'other', 'jpmovie', 'jpdrama', 'unknown'],
//typeDescription (string, optional): 类型描述 ,
//shift (number): 弹幕偏移时间（弹幕应延迟多少秒出现）。此数字为负数时表示弹幕应提前多少秒出现。
//}
@JsonClass(generateAdapter = true)
data class MatchResult(
    var animeId: Long = -1,
    var animeTitle: String = "",
    var episodeId: Int = 0,
    var episodeTitle: String = "",
    var type: String = "",
    var typeDescription: String = "",
    var shift: Long = 0
)