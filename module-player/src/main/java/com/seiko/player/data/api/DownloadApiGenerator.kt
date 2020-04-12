package com.seiko.player.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit

class DownloadApiGenerator {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://www.example.com")
        .client(okHttpClient.build())

    fun create(): DownloadApi {
        return retrofit.build().create()
    }

}