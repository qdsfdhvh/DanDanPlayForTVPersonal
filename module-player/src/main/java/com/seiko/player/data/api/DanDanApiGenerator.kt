package com.seiko.player.data.api

import com.seiko.player.BuildConfig
import com.seiko.player.util.constants.DANDAN_API_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class DanDanApiGenerator(
    okHttpClient: OkHttpClient,
    converterFactory: Converter.Factory
) {

    private val newOkHttpClient = okHttpClient.newBuilder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .addInterceptor(GzipInterceptor())
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.HEADERS
                })
            }
        }

    private val retrofit = Retrofit.Builder()
        .baseUrl(DANDAN_API_BASE_URL)
        .callFactory(newOkHttpClient.build())
        .addConverterFactory(converterFactory)

    fun create(): DanDanApiService {
        return retrofit.build().create(DanDanApiService::class.java)
    }

}