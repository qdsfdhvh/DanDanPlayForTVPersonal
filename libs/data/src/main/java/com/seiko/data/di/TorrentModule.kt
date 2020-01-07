package com.seiko.data.di

import android.content.ContentResolver
import android.content.Context
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import com.seiko.data.constants.TORRENT_CONFIG_DIR
import com.seiko.data.constants.TORRENT_DOWNLOAD_DIR
import com.seiko.data.constants.TORRENT_TEMP_DIR
import com.seiko.data.helper.TorrentHelper
import com.seiko.data.local.db.DbDataSource
import com.seiko.torrent.TorrentEngine
import com.seiko.torrent.TorrentEngineOptions
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

internal val torrentModule = module {

    factory(named(TORRENT_DOWNLOAD_DIR)) { createTorrentDownloadDir() }

    factory(named(TORRENT_TEMP_DIR)) { createTorrentTempDir(androidContext()) }

    factory(named(TORRENT_CONFIG_DIR)) { createTorrentConfigDir(androidContext()) }

    single { createContentResolver(androidContext()) }

    single { createTorrentSessionOptions(get(named(TORRENT_DOWNLOAD_DIR))) }

    single { createTorrentEngine(get()) }

    single { createTorrentHelper(get(), get()) }
}

private fun createContentResolver(context: Context): ContentResolver {
    return context.contentResolver
}

/**
 * 种子下载目录
 */
private fun createTorrentDownloadDir(): File {
//    return context.getExternalFilesDir(DIRECTORY_DOWNLOADS)!!
    return Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
}

/**
 * Torrent临时目录
 */
private fun createTorrentTempDir(context: Context): File {
    return File(context.getExternalFilesDir(null), "temp")
}

/**
 * Torrent配置目录
 */
private fun createTorrentConfigDir(context: Context): File {
    return File(context.getExternalFilesDir(null), "_config")
}

private fun createTorrentSessionOptions(downloadDir: File): TorrentEngineOptions {
    return TorrentEngineOptions(
        downloadDir = downloadDir
    )
}

private fun createTorrentEngine(options: TorrentEngineOptions): TorrentEngine {
    return TorrentEngine(options)
}

private fun createTorrentHelper(torrentEngine: TorrentEngine, dbHelper: DbDataSource): TorrentHelper {
    return TorrentHelper(torrentEngine, dbHelper)
}