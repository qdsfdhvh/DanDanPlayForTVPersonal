package com.seiko.player.di

import android.content.Context
import androidx.room.Room
import com.seiko.player.util.constants.DB_NAME_DEFAULT
import com.seiko.player.data.db.PlayerDatabase
import com.seiko.player.data.db.repo.DanmaRepository
import com.seiko.player.data.db.repo.SlaveRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {
    single { createPlayerDatabase(androidContext()) }
    single { createSlaveRepository(get()) }
    single { createDanmaRepository(get()) }
}

private fun createPlayerDatabase(context: Context): PlayerDatabase {
    return Room.databaseBuilder(context, PlayerDatabase::class.java, DB_NAME_DEFAULT).build()
}

private fun createSlaveRepository(database: PlayerDatabase): SlaveRepository {
    return SlaveRepository(database.slaveDao())
}

private fun createDanmaRepository(database: PlayerDatabase): DanmaRepository {
    return DanmaRepository(database.danmaDao())
}