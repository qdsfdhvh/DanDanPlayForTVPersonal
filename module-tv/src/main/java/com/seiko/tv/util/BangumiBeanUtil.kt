package com.seiko.tv.util

import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.db.model.BangumiIntroEntity
import com.seiko.tv.data.model.api.SearchAnimeDetails

fun BangumiDetailsEntity.toHomeImageBean(): HomeImageBean {
    return HomeImageBean(
        animeId = animeId,
        animeTitle = animeTitle,
        imageUrl = imageUrl,
        status = getBangumiStatus()
    )
}

fun BangumiDetailsEntity.getBangumiStatus(): String {
    if (!isOnAir) return "已完结 · ${episodes.size}话全"
    val onAirDay = when(airDay) {
        0 -> "每周日更新"
        1 -> "每周一更新"
        2 -> "每周二更新"
        3 -> "每周三更新"
        4 -> "每周四更新"
        5 -> "每周五更新"
        6 -> "每周六更新"
        else -> "更新时间未知"
    }
    return "连载中 · $onAirDay"
}

fun BangumiIntroEntity.toHomeImageBean(): HomeImageBean {
    return HomeImageBean(
        animeId = animeId,
        animeTitle = animeTitle,
        imageUrl = imageUrl,
        status = getBangumiStatus()
    )
}

fun BangumiIntroEntity.getBangumiStatus(): String {
    if (!isOnAir) return "已完结"
    val onAirDay = when(airDay) {
        0 -> "每周日更新"
        1 -> "每周一更新"
        2 -> "每周二更新"
        3 -> "每周三更新"
        4 -> "每周四更新"
        5 -> "每周五更新"
        6 -> "每周六更新"
        else -> "更新时间未知"
    }
    return "连载中 · $onAirDay"
}

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