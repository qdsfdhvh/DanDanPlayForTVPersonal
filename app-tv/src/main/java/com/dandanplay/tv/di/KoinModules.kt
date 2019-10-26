package com.dandanplay.tv.di

import com.dandanplay.tv.vm.BangumiAViewModel
import com.dandanplay.tv.vm.BangumiDetailViewModel
import com.dandanplay.tv.vm.BangumiSearchViewModel
import com.dandanplay.tv.vm.EpisodesSearchViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { BangumiAViewModel(get()) }

    viewModel { BangumiDetailViewModel(get()) }

    viewModel { BangumiSearchViewModel(get()) }

    viewModel { EpisodesSearchViewModel(get()) }
}