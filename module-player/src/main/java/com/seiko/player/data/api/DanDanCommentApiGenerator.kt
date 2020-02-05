package com.seiko.player.data.api

import com.seiko.player.util.constants.DANDAN_API_BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.fastjson.FastJsonConverterFactory
import java.util.concurrent.TimeUnit

class DanDanCommentApiGenerator(
    okHttpClient: OkHttpClient
) {

    private val newOkHttpClient = okHttpClient.newBuilder()
        .connectTimeout(5000, TimeUnit.MILLISECONDS)
        .readTimeout(5000, TimeUnit.MILLISECONDS)
        .addInterceptor(GzipInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(DANDAN_API_BASE_URL)
        .client(newOkHttpClient)
        .addConverterFactory(FastJsonConverterFactory.create())
        .build()

    fun create(): DanDanCommentApiService {
        return retrofit.create(DanDanCommentApiService::class.java)
    }

}