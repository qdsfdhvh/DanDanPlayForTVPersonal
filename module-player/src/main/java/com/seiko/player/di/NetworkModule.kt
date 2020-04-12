package com.seiko.player.di

import com.seiko.player.data.api.DanDanApiGenerator
import com.seiko.player.data.api.DanDanApi
import com.seiko.player.data.api.DownloadApi
import com.seiko.player.data.api.DownloadApiGenerator
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Converter

internal val networkModule = module {
    single { createDanDanApi(get(), get()) }
    single { createDownloadApi() }
}

private fun createDanDanApi(
    okHttpClient: OkHttpClient,
    converterFactory: Converter.Factory
): DanDanApi {
    return DanDanApiGenerator(okHttpClient, converterFactory).create()
}

private fun createDownloadApi(): DownloadApi {
    return DownloadApiGenerator().create()
}