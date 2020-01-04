package com.seiko.data.di

import com.seiko.data.http.api.DanDanApiService
import com.seiko.data.http.api.ResDanDanApiService
import com.seiko.data.repository.BangumiRepositoryImpl
import com.seiko.data.repository.SearchRepositoryImpl
import com.seiko.domain.repository.BangumiRepository
import com.seiko.domain.repository.SearchRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val repositoryModule = module {

    single { createBangumiRepository(get(named(API_DEFAULT))) }

    single { createSearchRepository(get(named(API_DEFAULT)), get()) }
}

private fun createBangumiRepository(api: DanDanApiService): BangumiRepository {
    return BangumiRepositoryImpl(api)
}

private fun createSearchRepository(api: DanDanApiService,
                                   resApi: ResDanDanApiService
): SearchRepository {
    return SearchRepositoryImpl(api, resApi)
}