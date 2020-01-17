package com.seiko.core.di

import android.content.Context
import com.google.gson.Gson
import com.seiko.core.data.api.DanDanApiGenerater
import com.seiko.core.data.api.DanDanApiService
import com.seiko.core.data.api.ResDanDanApiGenerater
import com.seiko.core.data.api.ResDanDanApiService
import com.seiko.core.data.comments.DanDanApiRemoteDataSource
import com.seiko.core.data.comments.ResDanDanApiRemoteDataSource
import com.seiko.core.http.cookie.CookiesManager
import com.seiko.core.http.cookie.PersistentCookieStore
import com.seiko.core.data.prefs.PrefDataSource
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.File
import java.util.concurrent.TimeUnit


internal val networkModule = module {
    single { createCache(androidContext()) }
    single { createCookieManager(get()) }
    single { createSingleHttpClient(get()) }

    single { createApiService(get(), get(), get(), get()) }
    single { createResApiService(get(), get()) }

    single { createDanDanApiRemoteDataSource(get()) }
    single { createResDanDanApiRemoteDataSource(get()) }
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

private fun createApiService(
    okHttpClient: OkHttpClient,
    gson: Gson,
    cookiesManager: CookiesManager,
    prefDataSource: PrefDataSource
): DanDanApiService {
    return DanDanApiGenerater(okHttpClient, gson, cookiesManager, prefDataSource).create()
}

private fun createResApiService(okHttpClient: OkHttpClient, gson: Gson): ResDanDanApiService {
    return ResDanDanApiGenerater(okHttpClient, gson).create()
}

private fun createDanDanApiRemoteDataSource(api: DanDanApiService): DanDanApiRemoteDataSource {
    return DanDanApiRemoteDataSource(api)
}

private fun createResDanDanApiRemoteDataSource(api: ResDanDanApiService): ResDanDanApiRemoteDataSource {
    return ResDanDanApiRemoteDataSource(api)
}
