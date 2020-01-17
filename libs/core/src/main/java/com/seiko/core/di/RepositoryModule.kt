package com.seiko.core.di

import com.seiko.core.data.db.AppDatabase
import com.seiko.core.data.comments.DanDanApiRemoteDataSource
import com.seiko.core.data.comments.ResDanDanApiRemoteDataSource
import com.seiko.core.repo.*
import com.seiko.core.repo.BangumiRepositoryImpl
import com.seiko.core.repo.SearchRepositoryImpl
import org.koin.dsl.module

internal val repositoryModule = module {

    single { createBangumiRepository(get(), get()) }

    single { createSearchRepository(get(), get()) }

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
