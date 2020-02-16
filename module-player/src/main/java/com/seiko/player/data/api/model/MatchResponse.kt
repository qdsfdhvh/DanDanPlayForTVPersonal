package com.seiko.player.data.api.model

import com.seiko.player.data.model.MatchResult

//MatchResponseV2 {
//isMatched (boolean, read only): 是否已精确关联到某个弹幕库 ,
//matches (Array[MatchResultV2], optional): 搜索匹配的结果 ,
//errorCode (integer): 错误代码，0表示没有发生错误，非0表示有错误，详细信息会包含在errorMessage属性中 ,
//success (boolean, read only): 接口是否调用成功 ,
//errorMessage (string, optional, read only): 当发生错误时，说明错误具体原因
//}
data class MatchResponse(
    var isMatched: Boolean = false,
    var matches: List<MatchResult> = emptyList(),
    var success: Boolean = false,
    var errorCode: Int = 0,
    var errorMessage: String = ""
)