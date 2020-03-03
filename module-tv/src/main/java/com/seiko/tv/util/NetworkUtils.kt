package com.seiko.tv.util

import com.seiko.common.data.Result
import com.seiko.tv.data.api.model.JsonResultResponse


internal suspend fun <T : JsonResultResponse, R : Any> apiCall(
    request: suspend () -> T,
    success: suspend (T) -> Result<R>
) : Result<R> {
    val response = try {
        request()
    } catch (e: Exception) {
        return Result.Error(e)
    }
    if (!response.success) {
        return Result.Error(Exception("${response.errorCode} ${response.errorMessage}"))
    }
    return success(response)
}