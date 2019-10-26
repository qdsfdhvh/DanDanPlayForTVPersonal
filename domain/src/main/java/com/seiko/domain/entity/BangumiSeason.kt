package com.seiko.domain.entity

//BangumiSeason {
//    year (integer): 年份 ,
//    month (integer): 月份 ,
//    seasonName (string, optional): 季度名称
//}
data class BangumiSeason(
    val year: Int,
    val month: Int,
    val seasonName: String
)