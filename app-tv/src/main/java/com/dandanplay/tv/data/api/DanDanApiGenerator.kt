package com.dandanplay.tv.data.api

import android.content.Context
import com.dandanplay.tv.constants.DANDAN_API_BASE_URL
import com.google.gson.Gson
import com.dandanplay.tv.data.prefs.PrefDataSource
import com.seiko.common.http.cookie.CookiesManager
import com.seiko.common.http.interceptor.RetrofitCacheInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class DanDanApiGenerator(
    context: Context,
    okHttpClient: OkHttpClient,
    gson: Gson,
    cookiesManager: CookiesManager,
    prefDataSource: PrefDataSource
) {
    private val newOkHttpClient = okHttpClient.newBuilder()
        .cookieJar(cookiesManager)
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .addInterceptor(RetrofitCacheInterceptor(context))
        .addInterceptor { chain ->
            val token = prefDataSource.token
            if (token.isNotEmpty()) {
                val original = chain.request()
                val builder = original.newBuilder()
                    .header("Authorization", "Bearer $token")
                return@addInterceptor chain.proceed(builder.build())
            }
            return@addInterceptor chain.proceed(chain.request())
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(DANDAN_API_BASE_URL)
        .client(newOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun create(): DanDanApiService {
        return retrofit.create(DanDanApiService::class.java)
    }
}