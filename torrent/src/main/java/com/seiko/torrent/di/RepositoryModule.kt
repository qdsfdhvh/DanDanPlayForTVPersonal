package com.seiko.torrent.di

import com.seiko.torrent.data.comments.TorrentRepository
import com.seiko.torrent.data.db.TorrentDatabase
import org.koin.dsl.module

val repositoryModule = module {
    single { createTorrentRepository(get()) }
}

private fun createTorrentRepository(database: TorrentDatabase): TorrentRepository {
    return TorrentRepository(database)
}