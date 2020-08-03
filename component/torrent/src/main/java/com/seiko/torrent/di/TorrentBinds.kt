package com.seiko.torrent.di

import com.seiko.torrent.download.DownloaderImpl
import com.seiko.torrent.download.Downloader
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class TorrentBinds {

    @Binds
    @Singleton
    @ExperimentalCoroutinesApi
    abstract fun getDownloader(impl: DownloaderImpl): Downloader
}