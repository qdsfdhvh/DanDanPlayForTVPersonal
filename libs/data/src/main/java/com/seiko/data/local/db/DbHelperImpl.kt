package com.seiko.data.local.db

import com.seiko.data.models.TorrentEntity

class DbHelperImpl(private val database: AppDatabase) : DbHelper {

    override suspend fun getTorrents(): List<TorrentEntity> {
        return database.torrentDao().all()
    }

    override suspend fun insertTorrent(entity: TorrentEntity) {
        database.torrentDao().put(entity)
    }

    override suspend fun deleteTorrent(hash: String) {
        database.torrentDao().delete(hash)
    }

    override suspend fun exitTorrent(hash: String): Boolean {
        return database.torrentDao().count(hash) > 0
    }

    override suspend fun getTorrent(hash: String): TorrentEntity? {
        return database.torrentDao().get(hash)
    }
}