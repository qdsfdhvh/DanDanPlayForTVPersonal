package com.seiko.tv.di

import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.data.comments.BangumiHistoryRepository
import com.seiko.tv.data.comments.DanDanApiRepository
import com.seiko.tv.data.comments.EpisodeTorrentRepository
import com.seiko.tv.data.comments.ResMagnetItemRepository
import com.seiko.tv.data.comments.SearchRepository
import org.koin.dsl.module

internal val repositoryModule = module {
    single { BangumiDetailsRepository(get()) }
    single { BangumiHistoryRepository(get()) }
    single { DanDanApiRepository(get()) }
    single { EpisodeTorrentRepository(get()) }
    single { ResMagnetItemRepository(get()) }
    single { SearchRepository(get(), get()) }
}