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
        .callFactory(okHttpClient)
        .addConverterFactory(converterFactory)

    fun create(): ResDanDanApiService {
        return retrofit.build().create(ResDanDanApiService::class.java)
    }
}