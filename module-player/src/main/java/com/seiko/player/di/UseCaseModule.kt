package com.seiko.player.di

import com.seiko.player.domain.danma.DownloadDanmaUseCase
import com.seiko.player.domain.danma.GetDanmaUseCase
import com.seiko.player.domain.subtitle.GetSubtitleUseCase
import com.seiko.player.domain.danma.GetVideoEpisodeIdUseCase
import com.seiko.player.domain.media.CompletionVideoMediaUseCase
import com.seiko.player.domain.media.CreateVideoThumbnailPathUseCase
import com.seiko.player.domain.media.GetVideoMediaListUseCase
import com.seiko.player.domain.media.QueryVideoMediaUseCase
import org.koin.dsl.module

internal val useCaseModule = module {
    single { DownloadDanmaUseCase() }
    single { GetDanmaUseCase() }
    single { GetVideoEpisodeIdUseCase() }

    //Deprecated
    single { CompletionVideoMediaUseCase() }
    single { CreateVideoThumbnailPathUseCase() }
    single { GetVideoMediaListUseCase() }
    single { QueryVideoMediaUseCase() }

    single { GetSubtitleUseCase() }
}