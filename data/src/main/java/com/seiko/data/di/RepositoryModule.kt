package com.seiko.data.di

import com.seiko.data.net.DanDanApiService
import com.seiko.data.repository.BangumiRepositoryImpl
import com.seiko.domain.repository.BangumiRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {

    single { createBangumiRepository(get(named(API_DEFAULT))) }
}

private fun createBangumiRepository(api: DanDanApiService): BangumiRepository {
    return BangumiRepositoryImpl(api)
}