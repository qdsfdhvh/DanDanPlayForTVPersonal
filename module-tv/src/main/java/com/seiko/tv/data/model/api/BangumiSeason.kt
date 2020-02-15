package com.seiko.tv.data.model.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//BangumiSeason {
//    year (integer): 年份 ,
//    month (integer): 月份 ,
//    seasonName (string, optional): 季度名称
//}
@Parcelize
data class BangumiSeason(
    val year: Int,
    val month: Int,
    val seasonName: String
) : Parcelable