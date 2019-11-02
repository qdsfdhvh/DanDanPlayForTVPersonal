package com.seiko.data.di

import com.seiko.data.utils.DEFAULT_TORRENT_RESUME_FILE
import com.seiko.data.utils.DEFAULT_TORRENT_SESSION_FILE
import com.seiko.download.TorrentEngine
import com.seiko.download.TorrentEngineOptions
import org.koin.dsl.module
import java.io.File

val torrentModule = module {

    single { createTorrentSessionOptions() }

    single { createTorrentEngine(get()) }

}

private fun createTorrentSessionOptions(): TorrentEngineOptions {
    return TorrentEngineOptions(
        torrentResumeFile = File(DEFAULT_TORRENT_RESUME_FILE),
        torrentSessionFile = File(DEFAULT_TORRENT_SESSION_FILE)
    )
}

private fun createTorrentEngine(options: TorrentEngineOptions): TorrentEngine {
    return TorrentEngine(options)
}