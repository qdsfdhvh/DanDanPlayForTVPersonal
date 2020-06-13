package com.seiko.tv.data.api.model

import com.seiko.tv.data.model.api.BangumiQueueIntro
import com.squareup.moshi.JsonClass

//BangumiQueueIntroResponseV2 {
//    hasMore (boolean): 是否有更多数据可以展示（显示界面上的“更多”按钮） ,
//    bangumiList (Array[BangumiQueueIntroV2], optional): 未看剧集列表 ,
//    errorCode (integer): 错误代码，0表示没有发生错误，非0表示有错误，详细信息会包含在errorMessage属性中 ,
//    success (boolean, read only): 接口是否调用成功 ,
//    errorMessage (string, optional, read only): 当发生错误时，说明错误具体原因
//}
@JsonClass(generateAdapter = true)
class BangumiQueueIntroResponse : JsonResultResponse() {
    var hasMore: Boolean = false
    var bangumiList: List<BangumiQueueIntro> = emptyList()
}