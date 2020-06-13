package com.seiko.player.di

import android.content.Context
import androidx.room.Room
import com.seiko.player.util.constants.DB_NAME_DEFAULT
import com.seiko.player.data.db.PlayerDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {
    single { createPlayerDatabase(androidContext()) }
}

private fun createPlayerDatabase(context: Context): PlayerDatabase {
    return PlayerDatabase.create(context, DB_NAME_DEFAULT)
}