package com.seiko.torrent.data.comments

import com.seiko.torrent.data.db.TorrentEntity
import com.seiko.torrent.data.db.TorrentDatabase

class TorrentRepository(database: TorrentDatabase) {

    private val torrentDao by lazy { database.torrentDao() }

    suspend fun getTorrents(): List<TorrentEntity> {
        return torrentDao.all()
    }

    suspend fun insertTorrent(task: TorrentEntity) {
        torrentDao.put(task)
    }

    suspend fun deleteTorrent(hash: String): Int {
        return torrentDao.delete(hash)
    }

    suspend fun exitTorrent(hash: String): Boolean {
        return torrentDao.count(hash) > 0
    }

    suspend fun getTorrent(hash: String): TorrentEntity? {
        return torrentDao.get(hash)
    }
}