package com.seiko.player.data.comments

import com.seiko.common.data.Result
import com.seiko.player.data.api.DanDanApiService
import com.seiko.player.data.api.model.MatchRequest
import com.seiko.player.data.model.DanmaCommentBean
import com.seiko.player.data.model.MatchResult
import com.squareup.moshi.Moshi
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import timber.log.Timber

class DanDanApiRepository(
    private val api: DanDanApiService
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
            return Result.Success(response.isMatched to response.matches)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

}