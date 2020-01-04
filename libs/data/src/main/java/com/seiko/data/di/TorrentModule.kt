package com.seiko.data.di

import android.content.ContentResolver
import android.content.Context
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import com.seiko.data.constants.TORRENT_DOWNOLAD_DIR
import com.seiko.data.constants.TORRENT_TEMP_DIR
import com.seiko.data.helper.TorrentHelper
import com.seiko.data.local.db.DbHelper
import com.seiko.torrent.TorrentEngine
import com.seiko.torrent.TorrentEngineOptions
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

internal val torrentModule = module {

    factory(named(TORRENT_DOWNOLAD_DIR)) { createTorrentDownloadDir(androidContext()) }

    factory(named(TORRENT_TEMP_DIR)) { createTorrentTempDir(androidContext()) }

    single { createContentResolver(androidContext()) }

    single { createTorrentSessionOptions(androidContext()) }

    single { createTorrentEngine(get()) }

    single { createTorrentHelper(get(), get()) }
}

private fun createContentResolver(context: Context): ContentResolver {
    return context.contentResolver
}

/**
 * 种子下载目录
 */
private fun createTorrentDownloadDir(context: Context): File {
    return context.getExternalFilesDir(DIRECTORY_DOWNLOADS)!!
}

/**
 * Torrent临时目录
 */
private fun createTorrentTempDir(context: Context): File {
    return File(context.getExternalFilesDir(null), "temp")
}

private fun createTorrentSessionOptions(context: Context): TorrentEngineOptions {
    return TorrentEngineOptions(
        downloadDir = context.getExternalFilesDir(null)!!
//        torrentResumeFile = File(DEFAULT_TORRENT_RESUME_FILE),
//        torrentSessionFile = File(DEFAULT_TORRENT_SESSION_FILE)
    )
}

private fun createTorrentEngine(options: TorrentEngineOptions): TorrentEngine {
    return TorrentEngine(options)
}

private fun createTorrentHelper(torrentEngine: TorrentEngine, dbHelper: DbHelper): TorrentHelper {
    return TorrentHelper(torrentEngine, dbHelper)
}