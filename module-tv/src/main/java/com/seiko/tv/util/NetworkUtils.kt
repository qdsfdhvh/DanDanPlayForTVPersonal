package com.seiko.tv.util

import com.seiko.common.data.Result
import com.seiko.tv.data.api.model.JsonResultResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

/**
 * 尝试从数据中获取
 * @param loadCache 加载缓存
 * @param isEffectCache 缓存数据是否有效
 * @param isUpdateLocalCache 是否需要刷新本地缓存
 * @param request 请求数据
 * @param success 请求成功转换数据
 */
internal fun <T : JsonResultResponse, R : Any> apiFlowCall(
    loadCache: suspend () -> T?,
    isEffectCache: (T) -> Boolean = { true },
    isUpdateLocalCache: suspend () -> Boolean,
    request: suspend () -> T,
    success: suspend (T) -> Result<R>
) : Flow<Result<R>> {
    return flow {
        val response: T? = loadCache.invoke()
        // 先读取本地缓存，如果数据有效加载页面
        if (response != null && isEffectCache.invoke(response)) {
            emit(success(response))

            // 是否需要更新本地缓存
            if (isUpdateLocalCache.invoke()) {
                return@flow
            }
        }

        // 请求api
        val result = apiCall(request, success)
        if (result is Result.Error) {
            Timber.e(result.exception)
        } else {
            emit(result)
        }
    }
}