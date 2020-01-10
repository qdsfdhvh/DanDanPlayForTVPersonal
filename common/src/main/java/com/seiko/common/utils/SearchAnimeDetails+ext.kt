package com.seiko.common.utils

import com.seiko.core.model.api.SearchAnimeDetails

//['tvseries', 'tvspecial', 'ova', 'movie', 'musicvideo', 'web', 'other', 'jpmovie', 'jpdrama', 'unknown'],
fun SearchAnimeDetails.getTypeName(): String {
    return when(type) {
        "tvseries" -> "连接"
        "tvspecial" -> ""
        "ova" -> "OVA"
        "movie" -> "电影"
        "musicvideo" -> "音乐视频"
        "web" -> "网页"
        "other" -> "其他"
        "jpmovie" -> ""
        "jpdrama" -> ""
        else -> ""
    }
}