package com.seiko.core.di

import com.seiko.core.data.api.*
import com.seiko.core.data.api.DanDanApiRemoteDataSource
import com.seiko.core.data.api.DanDanApiService
import com.seiko.core.data.api.ResDanDanApiRemoteDataSource
import com.seiko.core.data.api.ResDanDanApiService
import com.seiko.core.data.api.TorrentApiRemoteDataSource
import org.koin.dsl.module

val dataSourceModule = module {
    single { createDanDanApiRemoteDataSource(get()) }
    single { createResDanDanApiRemoteDataSource(get()) }
    single { createTorrentApiRemoteDataSource(get()) }
}

private fun createDanDanApiRemoteDataSource(api: DanDanApiService): DanDanApiRemoteDataSource {
    return DanDanApiRemoteDataSource(api)
}

private fun createResDanDanApiRemoteDataSource(api: ResDanDanApiService): ResDanDanApiRemoteDataSource {
    return ResDanDanApiRemoteDataSource(api)
}

private fun createTorrentApiRemoteDataSource(api: TorrentApiService): TorrentApiRemoteDataSource {
    return TorrentApiRemoteDataSource(api)
}