package com.seiko.player.di


import com.seiko.player.data.api.DanDanApiService
import com.seiko.player.data.comments.*
import com.seiko.player.data.db.PlayerDatabase
import org.koin.dsl.module

val repositoryModule = module {
    single { createDanDanApiRepository(get()) }
    single { createVideoDanmaDbRepository(get()) }
    single { createVideoMatchRepository(get()) }
    single { createSmbMd5Repository(get()) }
    single { createSmbMrlRepository(get()) }
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

private fun createSmbMd5Repository(database: PlayerDatabase): SmbMd5Repository {
    return SmbMd5Repository(database.smbMd5Dao())
}

private fun createSmbMrlRepository(database: PlayerDatabase): SmbMrlRepository {
    return SmbMrlRepository(database.smbMrlDao())
}