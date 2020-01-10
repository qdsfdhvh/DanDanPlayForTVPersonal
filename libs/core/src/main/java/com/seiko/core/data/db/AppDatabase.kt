package com.seiko.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.seiko.core.data.db.dao.TorrentDao
import com.seiko.core.data.db.model.TorrentEntity

@Database(entities = [TorrentEntity::class], version = 1)
@TypeConverters(PriorityListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun torrentDao(): TorrentDao

}