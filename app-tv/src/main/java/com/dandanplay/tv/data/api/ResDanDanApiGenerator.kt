package com.dandanplay.tv.data.api

import com.dandanplay.tv.util.constants.DANDAN_RES_BASE_URL
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class ResDanDanApiGenerator(
    okHttpClient: OkHttpClient,
    gson: Gson
) {
    private val retrofit = Retrofit.Builder()
        .baseUrl(DANDAN_RES_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun create(): ResDanDanApiService {
        return retrofit.create(ResDanDanApiService::class.java)
    }
}