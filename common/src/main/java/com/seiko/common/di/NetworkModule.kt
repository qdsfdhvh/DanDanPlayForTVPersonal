package com.seiko.common.di

import android.content.Context
import com.seiko.common.http.cookie.CookiesManager
import com.seiko.common.http.cookie.PersistentCookieStore
import com.squareup.moshi.Moshi
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


internal val networkModule = module {
    single { createCache(androidContext()) }
    single { createCookieManager(get()) }
    single { createSingleHttpClient(get()) }
    single { createConverterFactory(get()) }
}

private fun createCache(context: Context): Cache {
    val file = File(context.cacheDir, "HttpResponseCache")
    return Cache(file, 10 * 1024 * 1024)
}

private fun createCookieManager(cookieStore: PersistentCookieStore): CookiesManager {
    return CookiesManager(cookieStore)
}

private fun createSingleHttpClient(cache: Cache): OkHttpClient {
    return OkHttpClient.Builder()
        .cache(cache)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()
}

private fun createConverterFactory(moshi: Moshi): Converter.Factory {
    return MoshiConverterFactory.create(moshi)
}