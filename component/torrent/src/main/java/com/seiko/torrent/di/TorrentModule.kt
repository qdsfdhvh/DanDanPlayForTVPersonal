package com.seiko.torrent.di

import android.content.Context
import android.os.Environment
import com.seiko.download.torrent.TorrentEngineOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object TorrentModule {

    @Suppress("DEPRECATION")
    @Provides
    @Singleton
    @TorrentDownloadDir
    fun provideDownloadDir(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    }

    @Provides
    @Singleton
    @TorrentDataDir
    fun provideTorrentDataDir(@ApplicationContext context: Context): File {
        return context.getExternalFilesDir(null)!!
    }

    @Provides
    @Singleton
    @TorrentTempDir
    fun provideTorrentTempDir(@TorrentDataDir dataDir: File): File {
        return File(dataDir, "temp")
    }

    @Provides
    @Singleton
    @TorrentConfigDir
    fun provideTorrentConfigDir(@TorrentDataDir dataDir: File): File {
        return File(dataDir, "config")
    }

    @Provides
    @Singleton
    fun provideTorrentEngineOptions(@TorrentDataDir dataDir: File): TorrentEngineOptions {
        return TorrentEngineOptions(dataDir)
    }

}