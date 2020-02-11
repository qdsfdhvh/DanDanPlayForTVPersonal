package com.seiko.player.di

import com.seiko.player.domain.GetDanmaUseCase
import com.seiko.player.domain.GetSubtitleUserCase
import org.koin.dsl.module

internal val useCaseModule = module {
    single { GetDanmaUseCase() }
    single { GetSubtitleUserCase() }
}