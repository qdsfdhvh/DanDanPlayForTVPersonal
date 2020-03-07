package com.seiko.torrent.di

import android.content.Context
import android.os.Environment
import com.seiko.download.torrent.TorrentEngineOptions
import com.seiko.torrent.data.comments.TorrentRepository
import com.seiko.torrent.domain.GetTorrentInfoFileUseCase
import com.seiko.torrent.download.DownloadManager
import com.seiko.torrent.download.Downloader
import com.seiko.torrent.util.constants.TORRENT_CONFIG_DIR
import com.seiko.torrent.util.constants.TORRENT_DATA_DIR
import com.seiko.torrent.util.constants.TORRENT_DOWNLOAD_DIR
import com.seiko.torrent.util.constants.TORRENT_TEMP_DIR
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

internal val torrentModule = module {
    // Torrent下载目录
    factory(named(TORRENT_DOWNLOAD_DIR)) { createTorrentDownloadDir() }
    // Torrent文件目录
    single(named(TORRENT_DATA_DIR)) { createTorrentDataDir(androidContext()) }
    // Torrent临时目录
    single(named(TORRENT_TEMP_DIR)) { createTorrentTempDir(get(named(TORRENT_DATA_DIR))) }
    // Torrent参数配置路径(Trackers)
    single(named(TORRENT_CONFIG_DIR)) { createTorrentConfigDir(get(named(TORRENT_DATA_DIR))) }
    // Torrent引擎配置参数
    single { TorrentEngineOptions(get(named(TORRENT_DATA_DIR))) }
    // Torrent下载
    single { createDownloader(get(), get(), get()) }
}

/**
 * Torrent下载目录
 */
private fun createTorrentDownloadDir(): File {
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
}

/**
 * Torrent文件目录
 */
private fun createTorrentDataDir(context: Context): File {
    return context.getExternalFilesDir(null)!!
}

/**
 * Torrent临时目录
 */
private fun createTorrentTempDir(dataDir: File): File {
    return File(dataDir, "temp")
}

/**
 * Torrent配置目录
 */
private fun createTorrentConfigDir(dataDir: File): File {
    return File(dataDir, "config")
}

private fun createDownloader(options: TorrentEngineOptions,
                             torrentRepo: TorrentRepository,
                             getTorrentInfoFile: GetTorrentInfoFileUseCase
): Downloader {
    return DownloadManager(options, torrentRepo, getTorrentInfoFile)
}