package com.seiko.data.http.response

import com.seiko.domain.entity.BangumiQueueDetails

//BangumiQueueDetailsResponseV2 {
//    bangumiList (Array[BangumiQueueDetailsV2], optional): 未看番剧剧集列表 ,
//    unwatchedBangumiList (Array[BangumiQueueDetailsV2], optional): 已关注但从未看过的番剧列表 ,
//    errorCode (integer): 错误代码，0表示没有发生错误，非0表示有错误，详细信息会包含在errorMessage属性中 ,
//    success (boolean, read only): 接口是否调用成功 ,
//    errorMessage (string, optional, read only): 当发生错误时，说明错误具体原因
//}
class BangumiQueueDetailsResponse : JsonResultResponse() {
    var bangumiList: List<BangumiQueueDetails> = emptyList()
    var unwatchedBangumiList: List<BangumiQueueDetails> = emptyList()
}