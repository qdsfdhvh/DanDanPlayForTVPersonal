package com.seiko.tv.di

import com.seiko.tv.vm.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val viewModelModule = module {
    viewModel { BangumiAreaViewModel(get(), get()) }
    viewModel { BangumiAreaPageViewModel(get()) }
    viewModel { BangumiDetailViewModel(get(), get(), get(), get()) }
    viewModel { BangumiFavoriteViewModel(get()) }
    viewModel { BangumiHistoryViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { SearchBangumiViewModel(get(), get(), get()) }
    viewModel { SearchMagnetViewModel(get(), get()) }
}