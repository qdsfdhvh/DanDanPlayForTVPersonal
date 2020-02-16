package com.seiko.player.di

import com.seiko.player.domain.DownloadDanmaUseCase
import com.seiko.player.domain.GetDanmaUseCase
import com.seiko.player.domain.GetSubtitleUseCase
import com.seiko.player.domain.GetVideoEpisodeIdUseCase
import org.koin.dsl.module

internal val useCaseModule = module {
    single { DownloadDanmaUseCase() }
    single { GetDanmaUseCase() }
    single { GetSubtitleUseCase() }
    single { GetVideoEpisodeIdUseCase() }
}