package com.seiko.tv.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.seiko.tv.data.db.dao.*
import com.seiko.tv.data.db.model.*

@Database(entities = [
    BangumiDetailsEntity::class,
    BangumiEpisodeEntity::class,
    BangumiIntroEntity::class,
    BangumiTagEntity::class,
    BangumiHistoryEntity::class,
    BangumiKeyboardEntity::class,
    ResMagnetItemEntity::class,
    EpisodeTorrentEntity::class,
    HttpDbCacheEntity::class
], version = 5)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        fun create(context: Context, dbName: String): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, dbName)
                .addMigrations(
                    RoomMigration.MIGRATION_1_2,
                    RoomMigration.MIGRATION_2_3,
                    RoomMigration.MIGRATION_3_4,
                    RoomMigration.MIGRATION_4_5
                )
                .build()
        }
    }

    abstract fun bangumiDetailsDao(): BangumiDetailsDao

    abstract fun bangumiEpisodeDao(): BangumiEpisodeDao

    abstract fun bangumiIntroDao(): BangumiIntroDao

    abstract fun bangumiTagDao(): BangumiTagDao

    abstract fun bangumiHistoryDao(): BangumiHistoryDao

    abstract fun bangumiKeyboardDao(): BangumiKeyBoardDao

    abstract fun httpDbCacheDao(): HttpDbCacheDao

}