package com.seiko.common.utils

import com.seiko.domain.model.api.BangumiDetails


/**
 * 此番状态
 */
fun BangumiDetails.getBangumiStatus(): String {
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