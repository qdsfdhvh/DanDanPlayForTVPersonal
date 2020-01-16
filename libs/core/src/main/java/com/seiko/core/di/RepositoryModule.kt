package com.seiko.core.di

import com.seiko.core.data.api.DanDanApiService
import com.seiko.core.data.api.ResDanDanApiService
import com.seiko.core.data.db.AppDatabase
import com.seiko.core.data.api.DanDanApiRemoteDataSource
import com.seiko.core.data.api.ResDanDanApiRemoteDataSource
import com.seiko.core.repo.*
import com.seiko.core.repo.BangumiRepositoryImpl
import com.seiko.core.repo.SearchRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val repositoryModule = module {

    single { createBangumiRepository(get(), get()) }

    single { createSearchRepository(get(), get()) }

    single { createTorrentRepository(get()) }
}

private fun createBangumiRepository(
    dataSource: DanDanApiRemoteDataSource,
    database: AppDatabase
): BangumiRepository {
    return BangumiRepositoryImpl(dataSource, database)
}

private fun createSearchRepository(
    dataSource: DanDanApiRemoteDataSource,
    resDataSource: ResDanDanApiRemoteDataSource
): SearchRepository {
    return SearchRepositoryImpl(dataSource, resDataSource)
}

private fun createTorrentRepository(database: AppDatabase): TorrentRepository {
    return TorrentRepositoryImpl(database)
}