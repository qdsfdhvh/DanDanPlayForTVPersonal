package com.dandanplay.tv.di

import com.dandanplay.tv.data.comments.ResDanDanApiRemoteDataSource
import com.dandanplay.tv.data.db.AppDatabase
import com.dandanplay.tv.repo.*
import com.dandanplay.tv.repo.BangumiRepositoryImpl
import com.dandanplay.tv.repo.SearchRepositoryImpl
import org.koin.dsl.module

internal val repositoryModule = module {

    single { createBangumiRepository(get(), get()) }

    single { createSearchRepository(get(), get()) }

}

private fun createBangumiRepository(
    dataSource: com.dandanplay.tv.data.comments.DanDanApiRemoteDataSource,
    database: AppDatabase
): BangumiRepository {
    return BangumiRepositoryImpl(dataSource, database)
}

private fun createSearchRepository(
    dataSource: com.dandanplay.tv.data.comments.DanDanApiRemoteDataSource,
    resDataSource: ResDanDanApiRemoteDataSource
): SearchRepository {
    return SearchRepositoryImpl(dataSource, resDataSource)
}
