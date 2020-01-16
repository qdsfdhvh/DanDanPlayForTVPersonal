package com.seiko.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.seiko.core.data.db.dao.*
import com.seiko.core.data.db.model.*

@Database(entities = [
    BangumiDetailsEntity::class,
    BangumiEpisodeEntity::class,
    BangumiIntroEntity::class,
    BangumiTagEntity::class,
    ResMagnetItemEntity::class,
    EpisodeTorrentEntity::class,
    TorrentEntity::class
], version = 1)
@TypeConverters(PriorityListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bangumiDetailsDao(): BangumiDetailsDao

    abstract fun bangumiEpisodeDao(): BangumiEpisodeDao

    abstract fun bangumiIntroDao(): BangumiIntroDao

    abstract fun bangumiTagDao(): BangumiTagDao

    abstract fun resMagnetItemDao(): ResMagnetItemDao

    abstract fun episodeTorrentDao(): EpisodeTorrentDao

    abstract fun torrentDao(): TorrentDao
}