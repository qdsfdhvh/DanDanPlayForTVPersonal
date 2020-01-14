package com.seiko.core.di

import android.content.ContentResolver
import android.content.Context
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import com.seiko.core.constants.TORRENT_CONFIG_DIR
import com.seiko.core.constants.TORRENT_DATA_DIR
import com.seiko.core.constants.TORRENT_DOWNLOAD_DIR
import com.seiko.core.constants.TORRENT_TEMP_DIR
import com.seiko.download.torrent.TorrentEngineOptions
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

internal val torrentModule = module {

    factory(named(TORRENT_DOWNLOAD_DIR)) { createTorrentDownloadDir() }

    single(named(TORRENT_DATA_DIR)) { createTorrentDataDir(androidContext()) }

    single(named(TORRENT_TEMP_DIR)) { createTorrentTempDir(get(named(TORRENT_DATA_DIR))) }

    single(named(TORRENT_CONFIG_DIR)) { createTorrentConfigDir(get(named(TORRENT_DATA_DIR))) }

    single { createContentResolver(androidContext()) }

    single { createTorrentSessionOptions(get(named(TORRENT_DATA_DIR))) }

//    single { createTorrentEngine(get()) }

//    single { createTorrentHelper(get(), get(), get()) }
}

/**
 * 种子下载目录
 */
private fun createTorrentDownloadDir(): File {
    return Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
}

/**
 * Torrent文件路径
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

private fun createContentResolver(context: Context): ContentResolver {
    return context.contentResolver
}

private fun createTorrentSessionOptions(dataDir: File): TorrentEngineOptions {
    return TorrentEngineOptions(
        dataDir = dataDir
    )
}

//private fun createTorrentEngine(options: TorrentEngineOptions): TorrentEngine {
//    return TorrentEngine(options)
//}

//private fun createTorrentHelper(torrentEngine: TorrentEngine,
//                                torrentRepository: TorrentRepository,
//                                getTorrentInfo: GetTorrentInfoFileUseCase): TorrentHelper {
//    return TorrentHelper(torrentEngine, torrentRepository, getTorrentInfo)
//}