package com.seiko.tv.util

import com.seiko.common.data.Result
import com.seiko.tv.data.api.model.JsonResultResponse
import timber.log.Timber

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

internal suspend fun <T : JsonResultResponse, R : Any> apiCacheCall(
    isOutDate: suspend () -> Boolean,
    loadCache: suspend () -> T?,
    isEffectCache: (T) -> Boolean = { true },
    request: suspend () -> T,
    success: suspend (T) -> Result<R>
) : Result<R> {
    var response: T?
    // 缓存是否过时
    if (!isOutDate.invoke()) {
        response = loadCache.invoke()
        if (response != null && isEffectCache.invoke(response)) {
            return success(response)
        }
    }
    // 请求api
    val result = apiCall(request, success)
    if (result is Result.Error) {
        // 请求失败，尝试从缓存获取数据
        response = loadCache.invoke()
        if (response != null && isEffectCache.invoke(response)) {
            Timber.e(result.exception)
            return success(response)
        }
    }
    return result
}