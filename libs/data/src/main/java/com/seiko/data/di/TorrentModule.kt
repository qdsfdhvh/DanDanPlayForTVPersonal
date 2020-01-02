package com.seiko.data.di

import android.content.Context
import com.seiko.torrent.TorrentEngine
import com.seiko.torrent.TorrentEngineOptions
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val torrentModule = module {

    single { createTorrentSessionOptions(androidContext()) }

    single { createTorrentEngine(get()) }

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