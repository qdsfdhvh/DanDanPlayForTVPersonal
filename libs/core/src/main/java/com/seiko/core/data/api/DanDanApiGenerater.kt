package com.seiko.core.data.api

import com.google.gson.Gson
import com.seiko.core.constants.DANDAN_API_BASE_URL
import com.seiko.core.data.prefs.PrefDataSource
import com.seiko.core.http.cookie.CookiesManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class DanDanApiGenerater(
    okHttpClient: OkHttpClient,
    gson: Gson,
    cookiesManager: CookiesManager,
    prefDataSource: PrefDataSource
) {
    private val newOkHttpClient = okHttpClient.newBuilder()
        .cookieJar(cookiesManager)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val token = prefDataSource.token
                if (token.isNotEmpty()) {
                    val original = chain.request()
                    val builder = original.newBuilder()
                        .header("Authorization", "Bearer $token")
                    return chain.proceed(builder.build())
                }
                return chain.proceed(chain.request())
            }
        })
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