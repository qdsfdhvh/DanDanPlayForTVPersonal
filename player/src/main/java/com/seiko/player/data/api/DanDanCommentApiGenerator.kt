package com.seiko.player.data.api

import com.seiko.player.util.constants.DANDAN_API_BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit

class DanDanCommentApiGenerator(
    okHttpClient: OkHttpClient,
    converterFactory: Converter.Factory
) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(DANDAN_API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(converterFactory)
        .build()

    fun create(): DanDanCommentApiService {
        return retrofit.create(DanDanCommentApiService::class.java)
    }

}