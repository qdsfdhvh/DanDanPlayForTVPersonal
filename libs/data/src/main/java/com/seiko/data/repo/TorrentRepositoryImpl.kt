package com.seiko.data.repo

import com.seiko.data.local.db.AppDatabase
import com.seiko.data.model.TorrentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TorrentRepositoryImpl(database: AppDatabase) : TorrentRepository {

    private val torrentDao = database.torrentDao()

    override suspend fun getTorrents(): List<TorrentEntity> {
        return withContext(Dispatchers.IO) {
            torrentDao.all()
        }
    }

    override suspend fun insertTorrent(entity: TorrentEntity) {
        withContext(Dispatchers.IO) {
            torrentDao.put(entity)
        }
    }

    override suspend fun deleteTorrent(hash: String) {
        withContext(Dispatchers.IO) {
            torrentDao.delete(hash)
        }
    }

    override suspend fun exitTorrent(hash: String): Boolean {
        return withContext(Dispatchers.IO) {
            torrentDao.count(hash) > 0
        }
    }

    override suspend fun getTorrent(hash: String): TorrentEntity? {
        return withContext(Dispatchers.IO) {
            torrentDao.get(hash)
        }
    }
}