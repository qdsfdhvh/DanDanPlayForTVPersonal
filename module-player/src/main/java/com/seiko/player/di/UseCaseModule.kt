package com.seiko.player.di

import com.seiko.player.domain.danma.GetDanmaCommentsUseCase
import com.seiko.player.domain.danma.GetDanmaResultWithFileUseCase
import com.seiko.player.domain.subtitle.GetSubtitleUseCase
import com.seiko.player.domain.danma.GetVideoEpisodeIdUseCase
import org.koin.dsl.module

internal val useCaseModule = module {

    single { GetDanmaCommentsUseCase() }
    single { GetDanmaResultWithFileUseCase() }
    single { GetVideoEpisodeIdUseCase() }

    single { GetSubtitleUseCase() }
}