package com.seiko.tv.data.model.api

import com.squareup.moshi.JsonClass

//PopularTorrentItem {
//    name (string, optional): 种子资源名称 ,
//    magnet (string, optional): 磁力链接 ,
//    hot (integer): 种子热度（100为热门种子，有可能超过100）
//}
@JsonClass(generateAdapter = true)
data class PopularTorrentItem(
    var name: String = "",
    var magnet: String = "",
    var hot: Int = 0
)