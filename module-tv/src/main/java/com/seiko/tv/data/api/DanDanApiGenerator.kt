package com.seiko.tv.data.api

import com.seiko.tv.BuildConfig
import com.seiko.tv.util.constants.DANDAN_API_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit

internal class DanDanApiGenerator(
    okHttpClient: OkHttpClient,
    converterFactory: Converter.Factory
) {
    private val newOkHttpClient = okHttpClient.newBuilder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.HEADERS
            }
        })

    private val retrofit = Retrofit.Builder()
        .baseUrl(DANDAN_API_BASE_URL)
        .callFactory(newOkHttpClient.build())
        .addConverterFactory(converterFactory)

    fun create(): DanDanApiService {
        return retrofit.build().create(DanDanApiService::class.java)
    }
}