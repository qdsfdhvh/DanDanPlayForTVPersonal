package com.seiko.player.di


import com.seiko.player.data.api.DanDanApiService
import com.seiko.player.data.comments.DanDanApiRepository
import com.seiko.player.data.comments.VideoDanmaRepository
import com.seiko.player.data.comments.VideoMatchRepository
import com.seiko.player.data.comments.VideoMediaRepository
import com.seiko.player.data.db.PlayerDatabase
import com.squareup.moshi.Moshi
import org.koin.dsl.module

val repositoryModule = module {
    single { createDanDanApiRepository(get()) }
    single { createVideoDanmaDbRepository(get()) }
    single { createVideoMatchRepository(get()) }
    single { createVideoMediaRepository(get()) }
}

private fun createDanDanApiRepository(api: DanDanApiService): DanDanApiRepository {
    return DanDanApiRepository(api)
}

private fun createVideoDanmaDbRepository(database: PlayerDatabase): VideoDanmaRepository {
    return VideoDanmaRepository(database.danmaDao())
}

private fun createVideoMatchRepository(database: PlayerDatabase): VideoMatchRepository {
    return VideoMatchRepository(database.videoMatchDao())
}

private fun createVideoMediaRepository(database: PlayerDatabase): VideoMediaRepository {
    return VideoMediaRepository(database.videoMediaDao())
}