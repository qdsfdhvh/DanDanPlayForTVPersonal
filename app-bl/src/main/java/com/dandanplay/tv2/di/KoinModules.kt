package com.dandanplay.tv2.di

import com.dandanplay.tv2.vm.BangumiDetailViewModel
import com.dandanplay.tv2.vm.BangumiAViewModel
import com.dandanplay.tv2.vm.LoginViewModel
import com.dandanplay.tv2.vm.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { MainViewModel() }

    viewModel { LoginViewModel() }

    viewModel { BangumiAViewModel(get()) }

    viewModel { BangumiDetailViewModel(get()) }
}