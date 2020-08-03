package com.seiko.torrent.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [
    TorrentEntity::class
], version = 1)
@TypeConverters(PriorityListConverter::class)
abstract class TorrentDatabase : RoomDatabase() {

    companion object {
        fun create(context: Context, dbName: String): TorrentDatabase {
            return Room.databaseBuilder(context, TorrentDatabase::class.java, dbName)
                .build()
        }
    }

    abstract fun torrentDao(): TorrentDao
}