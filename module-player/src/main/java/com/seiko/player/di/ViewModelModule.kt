package com.seiko.player.di

import com.seiko.player.vm.PlayerViewModel
import com.seiko.player.vm.VideosViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val viewModelModule = module {
    viewModel { PlayerViewModel(get(), get(), get()) }
    viewModel { VideosViewModel(androidApplication()) }
}