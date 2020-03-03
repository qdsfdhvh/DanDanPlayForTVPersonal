package com.seiko.tv.data.api

import android.content.Context
import com.seiko.tv.BuildConfig
import com.seiko.tv.util.constants.DANDAN_API_BASE_URL
import com.seiko.tv.util.http.interceptor.CacheInterceptor
import com.seiko.tv.util.http.interceptor.OfflineCacheInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit

internal class DanDanApiGenerator(
    context: Context,
    okHttpClient: OkHttpClient,
    converterFactory: Converter.Factory
) {
    private val newOkHttpClient = okHttpClient.newBuilder()
        .retryOnConnectionFailure(false) // 连接失败后是否重新连接
        .addInterceptor(HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.HEADERS
            }
        })
        .addInterceptor(OfflineCacheInterceptor(context))
        .addNetworkInterceptor(CacheInterceptor(context))

    private val retrofit = Retrofit.Builder()
        .baseUrl(DANDAN_API_BASE_URL)
        .callFactory(newOkHttpClient.build())
        .addConverterFactory(converterFactory)

    fun create(): DanDanApiService {
        return retrofit.build().create(DanDanApiService::class.java)
    }
}