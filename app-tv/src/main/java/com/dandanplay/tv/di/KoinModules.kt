package com.dandanplay.tv.di

import com.dandanplay.tv.vm.BangumiAViewModel
import com.dandanplay.tv.vm.BangumiDetailViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { BangumiAViewModel(get()) }

    viewModel { BangumiDetailViewModel(get()) }
}