package com.seiko.player.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.seiko.player.data.db.dao.VideoDanmakuDao
import com.seiko.player.data.db.dao.VideoMatchDao
import com.seiko.player.data.db.dao.VideoMediaDao
import com.seiko.player.data.db.model.VideoDanmaku
import com.seiko.player.data.db.model.VideoMatch
import com.seiko.player.data.db.model.VideoMedia

@Database(entities = [
    VideoDanmaku::class,
    VideoMatch::class,
    VideoMedia::class
], version = 3)
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

    abstract fun videoMediaDao(): VideoMediaDao
}