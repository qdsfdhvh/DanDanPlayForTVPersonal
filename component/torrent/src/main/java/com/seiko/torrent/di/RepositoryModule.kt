package com.seiko.torrent.di

import com.seiko.torrent.data.comments.TorrentRepository
import com.seiko.torrent.data.db.TorrentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTorrentRepository(
        torrentDao: TorrentDao
    ): TorrentRepository {
        return TorrentRepository(torrentDao)
    }

}