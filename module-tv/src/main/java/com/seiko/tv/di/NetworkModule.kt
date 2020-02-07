package com.seiko.tv.di

import android.content.Context
import com.seiko.tv.data.api.DanDanApiGenerator
import com.seiko.tv.data.api.DanDanApiService
import com.seiko.tv.data.api.ResDanDanApiGenerator
import com.seiko.tv.data.api.ResDanDanApiService
import com.seiko.tv.data.comments.DanDanApiRemoteDataSource
import com.seiko.tv.data.comments.ResDanDanApiRemoteDataSource
import com.seiko.tv.data.prefs.PrefDataSource
import com.seiko.tv.util.http.cookie.CookiesManager
import com.seiko.tv.util.http.cookie.PersistentCookieStore
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Converter

internal val networkModule = module {
    single { createCookieManager(get()) }
    single { createApiService(androidContext(), get(), get(), get(), get()) }
    single { createResApiService(get(), get()) }
    single { createDanDanApiRemoteDataSource(get()) }
    single { createResDanDanApiRemoteDataSource(get()) }
}

private fun createCookieManager(cookieStore: PersistentCookieStore): CookiesManager {
    return CookiesManager(cookieStore)
}

private fun createApiService(
    context: Context,
    okHttpClient: OkHttpClient,
    converterFactory: Converter.Factory,
    cookiesManager: CookiesManager,
    prefDataSource: PrefDataSource
): DanDanApiService {
    return DanDanApiGenerator(context,
        okHttpClient,
        converterFactory,
        cookiesManager,
        prefDataSource
    ).create()
}

private fun createResApiService(
    okHttpClient: OkHttpClient,
    converterFactory: Converter.Factory
): ResDanDanApiService {
    return ResDanDanApiGenerator(
        okHttpClient,
        converterFactory
    ).create()
}

private fun createDanDanApiRemoteDataSource(api: DanDanApiService): DanDanApiRemoteDataSource {
    return DanDanApiRemoteDataSource(api)
}

private fun createResDanDanApiRemoteDataSource(api: ResDanDanApiService): ResDanDanApiRemoteDataSource {
    return ResDanDanApiRemoteDataSource(api)
}