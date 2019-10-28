package com.dandanplay.tv.di

import com.dandanplay.tv.vm.*
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { BangumiViewModel(get()) }

    viewModel { BangumiDetailViewModel(get()) }

    viewModel { SearchBangumiViewModel(get()) }

    viewModel { SearchEpisodesViewModel(get(), get(), get(), get()) }

    viewModel { TorrentFileCheckViewModel(get()) }
}