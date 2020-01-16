package com.seiko.core.data.api.model

import com.seiko.core.data.db.model.BangumiDetailsEntity

//BangumiDetailsResponse {
//    bangumi (BangumiDetails, optional): 番剧详情 ,
//    errorCode (integer): 错误代码，0表示没有发生错误，非0表示有错误，详细信息会包含在errorMessage属性中 ,
//    success (boolean, read only): 接口是否调用成功 ,
//    errorMessage (string, optional, read only): 当发生错误时，说明错误具体原因
//}
class BangumiDetailsResponse : JsonResultResponse() {
    var bangumi: BangumiDetailsEntity = BangumiDetailsEntity.empty()
}