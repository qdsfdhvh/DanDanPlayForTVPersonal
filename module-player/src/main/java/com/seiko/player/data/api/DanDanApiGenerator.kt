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
        .connectTimeout(5000, TimeUnit.MILLISECONDS)
        .readTimeout(5000, TimeUnit.MILLISECONDS)
        .addInterceptor(GzipInterceptor())
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.HEADERS
                })
            }
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(DANDAN_API_BASE_URL)
        .client(newOkHttpClient)
        .addConverterFactory(converterFactory)
        .build()

    fun create(): DanDanApiService {
        return retrofit.create(DanDanApiService::class.java)
    }

}