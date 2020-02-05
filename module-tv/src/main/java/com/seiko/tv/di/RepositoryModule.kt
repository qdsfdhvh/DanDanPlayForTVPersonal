package com.seiko.tv.di

import com.seiko.tv.data.comments.ResDanDanApiRemoteDataSource
import com.seiko.tv.data.db.AppDatabase
import com.seiko.tv.data.repo.*
import com.seiko.tv.data.repo.BangumiRepositoryImpl
import com.seiko.tv.data.repo.SearchRepositoryImpl
import org.koin.dsl.module

internal val repositoryModule = module {
    single { createBangumiRepository(get(), get()) }
    single { createSearchRepository(get(), get()) }
    single { createEpisodeTorrentRepository(get()) }
}

private fun createBangumiRepository(
    dataSource: com.seiko.tv.data.comments.DanDanApiRemoteDataSource,
    database: AppDatabase
): BangumiRepository {
    return BangumiRepositoryImpl(dataSource, database)
}

private fun createSearchRepository(
    dataSource: com.seiko.tv.data.comments.DanDanApiRemoteDataSource,
    resDataSource: ResDanDanApiRemoteDataSource
): SearchRepository {
    return SearchRepositoryImpl(dataSource, resDataSource)
}

private fun createEpisodeTorrentRepository(database: AppDatabase): EpisodeTorrentRepository {
    return EpisodeTorrentRepository(database.episodeTorrentDao())
}
