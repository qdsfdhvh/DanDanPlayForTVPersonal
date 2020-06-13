package com.seiko.player.data.comments

import com.seiko.common.data.Result
import com.seiko.player.data.api.DanDanApi
import com.seiko.player.data.api.model.MatchRequest
import com.seiko.player.data.model.DanmaCommentBean
import com.seiko.player.data.model.MatchResult

class DanDanApiRepository(
    private val api: DanDanApi
) {

    suspend fun downloadDanma(episodeId: Int): Result<List<DanmaCommentBean>> {
        return try {
            val response = api.downloadDanma(episodeId)
            Result.Success(response.comments)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getVideoMatchList(request: MatchRequest) : Result<Pair<Boolean, List<MatchResult>>> {
        try {
            val response = api.getVideoMatch(request)
            if (!response.success) {
                return Result.Error(Exception("(${response.errorCode}) ${response.errorMessage}"))
            }
            return Result.Success(response.isMatched to (response.matches ?: emptyList()))
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

}