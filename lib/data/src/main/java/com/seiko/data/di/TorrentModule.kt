package com.seiko.data.di

import com.seiko.torrent.TorrentEngine
import com.seiko.torrent.TorrentEngineOptions
import org.koin.dsl.module

val torrentModule = module {

    single { createTorrentSessionOptions() }

    single { createTorrentEngine(get()) }

}

private fun createTorrentSessionOptions(): TorrentEngineOptions {
    return TorrentEngineOptions(
//        torrentResumeFile = File(DEFAULT_TORRENT_RESUME_FILE),
//        torrentSessionFile = File(DEFAULT_TORRENT_SESSION_FILE)
    )
}

private fun createTorrentEngine(options: TorrentEngineOptions): TorrentEngine {
    return TorrentEngine(options)
}