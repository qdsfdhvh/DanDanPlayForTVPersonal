package com.seiko.core.data.api

import com.google.gson.Gson
import com.seiko.core.constants.DANDAN_RES_BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class ResDanDanApiGenerater(
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