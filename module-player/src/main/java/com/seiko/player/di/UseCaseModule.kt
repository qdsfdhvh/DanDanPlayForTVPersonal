package com.seiko.player.di

import com.seiko.player.domain.danma.*
import com.seiko.player.domain.subtitle.GetSubtitleUseCase
import org.koin.dsl.module

internal val useCaseModule = module {

    single { GetDanmaCommentsUseCase() }
    single { GetDanmaResultUseCase() }
    single { GetDanmaResultWithFileUseCase() }
    single { GetDanmaResultWithSmbUseCase() }
    single { GetVideoEpisodeIdUseCase() }

    single { GetSubtitleUseCase() }
}