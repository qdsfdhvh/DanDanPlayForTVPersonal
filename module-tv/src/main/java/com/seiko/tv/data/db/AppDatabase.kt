package com.seiko.tv.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.seiko.tv.data.db.dao.*
import com.seiko.tv.data.db.model.*

@Database(entities = [
    BangumiDetailsEntity::class,
    BangumiEpisodeEntity::class,
    BangumiIntroEntity::class,
    BangumiTagEntity::class,
    ResMagnetItemEntity::class,
    EpisodeTorrentEntity::class
], version = 1)

abstract class AppDatabase : RoomDatabase() {

    abstract fun bangumiDetailsDao(): BangumiDetailsDao

    abstract fun bangumiEpisodeDao(): BangumiEpisodeDao

    abstract fun bangumiIntroDao(): BangumiIntroDao

    abstract fun bangumiTagDao(): BangumiTagDao

    abstract fun resMagnetItemDao(): ResMagnetItemDao

    abstract fun episodeTorrentDao(): EpisodeTorrentDao

}