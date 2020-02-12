package com.seiko.player.di


import com.seiko.player.data.comments.DanmaRepository
import com.seiko.player.data.comments.SlaveRepository
import com.seiko.player.data.db.PlayerDatabase
import org.koin.dsl.module

val repositoryModule = module {
    single { createSlaveRepository(get()) }
    single { createDanmaRepository(get()) }
}

private fun createSlaveRepository(database: PlayerDatabase): SlaveRepository {
    return SlaveRepository(database.slaveDao())
}

private fun createDanmaRepository(database: PlayerDatabase): DanmaRepository {
    return DanmaRepository(database.danmaDao())
}