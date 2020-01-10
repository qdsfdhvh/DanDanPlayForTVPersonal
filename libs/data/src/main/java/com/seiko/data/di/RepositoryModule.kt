package com.seiko.data.di

import com.seiko.data.http.api.DanDanApiService
import com.seiko.data.http.api.ResDanDanApiService
import com.seiko.data.local.db.AppDatabase
import com.seiko.data.repo.BangumiRepositoryImpl
import com.seiko.data.repo.SearchRepositoryImpl
import com.seiko.data.repo.TorrentRepository
import com.seiko.data.repo.TorrentRepositoryImpl
import com.seiko.data.repo.BangumiRepository
import com.seiko.data.repo.SearchRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val repositoryModule = module {

    single { createBangumiRepository(get(named(API_DEFAULT))) }

    single { createSearchRepository(get(named(API_DEFAULT)), get()) }

    single { createTorrentRepository(get()) }
}

private fun createBangumiRepository(api: DanDanApiService): BangumiRepository {
    return BangumiRepositoryImpl(api)
}

private fun createSearchRepository(api: DanDanApiService,
                                   resApi: ResDanDanApiService): SearchRepository {
    return SearchRepositoryImpl(api, resApi)
}

private fun createTorrentRepository(database: AppDatabase): TorrentRepository {
    return TorrentRepositoryImpl(database)
}