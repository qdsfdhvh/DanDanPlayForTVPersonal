package com.seiko.tv.data.model.api

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

//BangumiSeason {
//    year (integer): 年份 ,
//    month (integer): 月份 ,
//    seasonName (string, optional): 季度名称
//}
@JsonClass(generateAdapter = true)
@Parcelize
data class BangumiSeason(
    val year: Int,
    val month: Int,
    val seasonName: String
) : Parcelable