package com.dandanplay.tv.di

import android.content.Context
import com.dandanplay.tv.data.api.DanDanApiGenerator
import com.dandanplay.tv.data.api.DanDanApiService
import com.dandanplay.tv.data.api.ResDanDanApiGenerator
import com.dandanplay.tv.data.api.ResDanDanApiService
import com.google.gson.Gson
import com.dandanplay.tv.data.prefs.PrefDataSource
import com.seiko.common.http.cookie.CookiesManager
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val networkModule = module {
    single { createApiService(androidContext(), get(), get(), get(), get()) }
    single { createResApiService(get(), get()) }
}

private fun createApiService(
    context: Context,
    okHttpClient: OkHttpClient,
    gson: Gson,
    cookiesManager: CookiesManager,
    prefDataSource: PrefDataSource
): DanDanApiService {
    return DanDanApiGenerator(context, okHttpClient, gson, cookiesManager, prefDataSource).create()
}

private fun createResApiService(okHttpClient: OkHttpClient, gson: Gson): ResDanDanApiService {
    return ResDanDanApiGenerator(okHttpClient, gson).create()
}
