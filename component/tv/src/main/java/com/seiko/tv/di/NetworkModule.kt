package com.seiko.tv.di

import com.seiko.tv.data.api.DanDanApiGenerator
import com.seiko.tv.data.api.DanDanApiService
import com.seiko.tv.data.api.ResDanDanApiGenerator
import com.seiko.tv.data.api.ResDanDanApiService
import com.seiko.tv.util.http.cookie.CookiesManager
import com.seiko.tv.util.http.cookie.PersistentCookieStore
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Converter

internal val networkModule = module {
    single { createCookieManager(get()) }
    single { createApiService(get(), get()) }
    single { createResApiService(get(), get()) }
}

private fun createCookieManager(cookieStore: PersistentCookieStore): CookiesManager {
    return CookiesManager(cookieStore)
}

private fun createApiService(okHttpClient: OkHttpClient, converterFactory: Converter.Factory): DanDanApiService {
    return DanDanApiGenerator(okHttpClient, converterFactory).create()
}

private fun createResApiService(okHttpClient: OkHttpClient, converterFactory: Converter.Factory): ResDanDanApiService {
    return ResDanDanApiGenerator(okHttpClient, converterFactory).create()
}