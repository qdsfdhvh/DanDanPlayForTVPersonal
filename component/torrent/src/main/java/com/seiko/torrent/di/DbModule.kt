package com.seiko.torrent.di

import android.app.Application
import com.seiko.torrent.data.db.TorrentDao
import com.seiko.torrent.data.db.TorrentDatabase
import com.seiko.torrent.util.constants.DB_NAME_DEFAULT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun provideTorrentDatabase(application: Application): TorrentDatabase {
        return TorrentDatabase.create(application, DB_NAME_DEFAULT)
    }

    @Provides
    @Singleton
    fun provideTorrentDao(database: TorrentDatabase): TorrentDao {
        return database.torrentDao()
    }

}