package com.seiko.torrent.di

import com.seiko.common.service.TorrentService
import com.seiko.torrent.download.DownloaderImpl
import com.seiko.torrent.download.Downloader
import com.seiko.torrent.service.TorrentServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
interface TorrentBinds {

    @ExperimentalCoroutinesApi
    @get:[Binds Singleton]
    val DownloaderImpl.downloader : Downloader

    @get:[Binds Singleton]
    val TorrentServiceImpl.torrentService: TorrentService
}