package com.seiko.core.repo

import com.seiko.core.data.db.AppDatabase
import com.seiko.core.data.db.model.TorrentEntity

class TorrentRepositoryImpl(database: AppDatabase) : TorrentRepository {

    private val torrentDao = database.torrentDao()

    override suspend fun getTorrents(): List<TorrentEntity> {
        return torrentDao.all()
    }

    override suspend fun insertTorrent(entity: TorrentEntity) {
        torrentDao.put(entity)
    }

    override suspend fun deleteTorrent(hash: String) {
        torrentDao.delete(hash)
    }

    override suspend fun exitTorrent(hash: String): Boolean {
        return torrentDao.count(hash) > 0
    }

    override suspend fun getTorrent(hash: String): TorrentEntity? {
        return torrentDao.get(hash)
    }
}