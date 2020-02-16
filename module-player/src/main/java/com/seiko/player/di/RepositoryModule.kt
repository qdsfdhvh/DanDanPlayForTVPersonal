package com.seiko.player.di


import com.seiko.player.data.api.DanDanApiService
import com.seiko.player.data.comments.DanDanApiRepository
import com.seiko.player.data.comments.DanmaDbRepository
import com.seiko.player.data.comments.SlaveRepository
import com.seiko.player.data.comments.VideoMatchRepository
import com.seiko.player.data.db.PlayerDatabase
import com.squareup.moshi.Moshi
import org.koin.dsl.module

val repositoryModule = module {
    single { createDanDanApiRepository(get(), get()) }
    single { createSlaveRepository(get()) }
    single { createDanmaRepository(get()) }
    single { createVideoMatchRepository(get()) }
}

private fun createDanDanApiRepository(api: DanDanApiService, moshi: Moshi): DanDanApiRepository {
    return DanDanApiRepository(api, moshi)
}

private fun createSlaveRepository(database: PlayerDatabase): SlaveRepository {
    return SlaveRepository(database.slaveDao())
}

private fun createDanmaRepository(database: PlayerDatabase): DanmaDbRepository {
    return DanmaDbRepository(database.danmaDao())
}

private fun createVideoMatchRepository(database: PlayerDatabase): VideoMatchRepository {
    return VideoMatchRepository(database.videoMatchDao())
}