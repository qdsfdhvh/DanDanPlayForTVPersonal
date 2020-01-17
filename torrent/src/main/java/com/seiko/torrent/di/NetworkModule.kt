package com.seiko.torrent.di

import com.seiko.torrent.data.api.TorrentApiGenerater
import com.seiko.torrent.data.api.TorrentApiService
import com.seiko.torrent.data.comments.TorrentApiRemoteDataSource
import okhttp3.OkHttpClient
import org.koin.dsl.module

internal val networkModule = module {
    single { createTorrentApiService(get()) }
    single { createTorrentApiRemoteDataSource(get()) }
}

private fun createTorrentApiService(okHttpClient: OkHttpClient): TorrentApiService {
    return TorrentApiGenerater(okHttpClient).create()
}

private fun createTorrentApiRemoteDataSource(api: TorrentApiService): TorrentApiRemoteDataSource {
    return TorrentApiRemoteDataSource(api)
}