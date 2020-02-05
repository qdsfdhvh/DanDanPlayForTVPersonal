package com.seiko.tv.data.api

import com.seiko.tv.util.constants.DANDAN_RES_BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit

internal class ResDanDanApiGenerator(
    okHttpClient: OkHttpClient,
    converterFactory: Converter.Factory
) {
    private val retrofit = Retrofit.Builder()
        .baseUrl(DANDAN_RES_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(converterFactory)
        .build()

    fun create(): ResDanDanApiService {
        return retrofit.create(ResDanDanApiService::class.java)
    }
}