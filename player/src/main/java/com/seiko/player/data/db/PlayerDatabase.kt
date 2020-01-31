package com.seiko.player.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [
    Slave::class
], version = 1)
abstract class PlayerDatabase : RoomDatabase() {
    abstract fun slaveDao(): SlaveDao
}