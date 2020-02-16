package com.seiko.player.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.seiko.player.data.db.dao.DanmakuDao
import com.seiko.player.data.db.dao.SlaveDao
import com.seiko.player.data.db.dao.VideoMatchDao
import com.seiko.player.data.db.model.Danmaku
import com.seiko.player.data.db.model.Slave
import com.seiko.player.data.db.model.VideoMatch

@Database(entities = [
    Slave::class,
    Danmaku::class,
    VideoMatch::class
], version = 2)
@TypeConverters(DanmaDownloadBeanConverter::class)
abstract class PlayerDatabase : RoomDatabase() {

    companion object {
        fun create(context: Context, dbName: String): PlayerDatabase {
            return Room.databaseBuilder(context, PlayerDatabase::class.java, dbName)
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun slaveDao(): SlaveDao

    abstract fun danmaDao(): DanmakuDao

    abstract fun videoMatchDao(): VideoMatchDao

}