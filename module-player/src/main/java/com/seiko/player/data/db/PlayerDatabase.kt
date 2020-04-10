package com.seiko.player.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.seiko.player.data.db.dao.SmbMd5Dao
import com.seiko.player.data.db.dao.SmbMrlDao
import com.seiko.player.data.db.dao.VideoDanmakuDao
import com.seiko.player.data.db.dao.VideoMatchDao
import com.seiko.player.data.db.model.SmbMd5
import com.seiko.player.data.db.model.SmbMrl
import com.seiko.player.data.db.model.VideoDanmaku
import com.seiko.player.data.db.model.VideoMatch

@Database(entities = [
    VideoDanmaku::class,
    VideoMatch::class,
    SmbMd5::class,
    SmbMrl::class
], version = 8)
@TypeConverters(DanmaDownloadBeanConverter::class)
abstract class PlayerDatabase : RoomDatabase() {

    companion object {
        fun create(context: Context, dbName: String): PlayerDatabase {
            return Room.databaseBuilder(context, PlayerDatabase::class.java, dbName)
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun danmaDao(): VideoDanmakuDao

    abstract fun videoMatchDao(): VideoMatchDao

    abstract fun smbMd5Dao(): SmbMd5Dao

    abstract fun smbMrlDao(): SmbMrlDao

}