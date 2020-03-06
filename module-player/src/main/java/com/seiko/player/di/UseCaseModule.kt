package com.seiko.player.di

import com.seiko.player.domain.danma.DownloadDanmaUseCase
import com.seiko.player.domain.danma.GetDanmaCommentsUseCase
import com.seiko.player.domain.danma.GetDanmaResultUseCase
import com.seiko.player.domain.subtitle.GetSubtitleUseCase
import com.seiko.player.domain.danma.GetVideoEpisodeIdUseCase
import org.koin.dsl.module

internal val useCaseModule = module {

    single { DownloadDanmaUseCase() }
    single { GetDanmaCommentsUseCase() }
    single { GetDanmaResultUseCase() }
    single { GetVideoEpisodeIdUseCase() }

    single { GetSubtitleUseCase() }
}