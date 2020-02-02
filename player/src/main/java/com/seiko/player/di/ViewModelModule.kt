package com.seiko.player.di

import com.seiko.player.vm.PlayerViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val viewModelModule = module {
    viewModel { PlayerViewModel(get()) }
}