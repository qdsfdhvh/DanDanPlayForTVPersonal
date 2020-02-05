package com.seiko.player.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.seiko.player.data.db.dao.DanmaDao
import com.seiko.player.data.db.dao.SlaveDao
import com.seiko.player.data.db.model.Danma
import com.seiko.player.data.db.model.Slave

@Database(entities = [
    Slave::class,
    Danma::class
], version = 1)
@TypeConverters(DanmaDownloadBeanConverter::class)
abstract class PlayerDatabase : RoomDatabase() {
    abstract fun slaveDao(): SlaveDao

    abstract fun danmaDao(): DanmaDao
}