package com.seiko.player.di

import com.seiko.player.domain.DownloadDanmaUseCase
import org.koin.dsl.module

internal val useCaseModule = module {
    single { DownloadDanmaUseCase() }
}