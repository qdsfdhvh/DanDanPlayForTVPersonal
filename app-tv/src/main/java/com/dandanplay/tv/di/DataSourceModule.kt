package com.dandanplay.tv.di

import com.dandanplay.tv.data.api.DanDanApiService
import com.dandanplay.tv.data.api.ResDanDanApiService
import com.dandanplay.tv.data.comments.DanDanApiRemoteDataSource
import com.dandanplay.tv.data.comments.ResDanDanApiRemoteDataSource
import com.dandanplay.tv.data.prefs.PrefDataSource
import com.dandanplay.tv.data.prefs.PrefDataSourceImpl
import com.tencent.mmkv.MMKV
import org.koin.dsl.module

val dataSourceModule = module {
    single { createPrefDataSource(get()) }
    single { createDanDanApiRemoteDataSource(get()) }
    single { createResDanDanApiRemoteDataSource(get()) }
}

private fun createPrefDataSource(prefs: MMKV): PrefDataSource {
    return PrefDataSourceImpl(prefs)
}

private fun createDanDanApiRemoteDataSource(api: DanDanApiService): DanDanApiRemoteDataSource {
    return DanDanApiRemoteDataSource(api)
}

private fun createResDanDanApiRemoteDataSource(api: ResDanDanApiService): ResDanDanApiRemoteDataSource {
    return ResDanDanApiRemoteDataSource(api)
}