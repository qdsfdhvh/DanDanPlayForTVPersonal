package com.dandanplay.tv.di

import com.dandanplay.tv.vm.BangumiAViewModel
import org.koin.dsl.module

val viewModelModule = module {

    single { BangumiAViewModel(get()) }
}