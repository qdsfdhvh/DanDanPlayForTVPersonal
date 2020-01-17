package com.seiko.torrent.di

import android.content.Context
import androidx.room.Room
import com.seiko.torrent.constants.DB_NAME_DEFAULT
import com.seiko.torrent.data.db.TorrentDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val dbModule = module {
    single { createTorrentDatabase(androidContext()) }
}

private fun createTorrentDatabase(context: Context): TorrentDatabase {
    return Room.databaseBuilder(context, TorrentDatabase::class.java, DB_NAME_DEFAULT).build()
}
