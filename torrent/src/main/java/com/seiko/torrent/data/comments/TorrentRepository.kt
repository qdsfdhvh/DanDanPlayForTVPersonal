package com.seiko.torrent.data.comments

import com.seiko.download.torrent.model.TorrentTask
import com.seiko.torrent.data.model.TorrentEntity
import com.seiko.torrent.data.db.TorrentDatabase

class TorrentRepository(database: TorrentDatabase) {

    private val torrentDao by lazy { database.torrentDao() }

    suspend fun getTorrents(): List<TorrentTask> {
        val entities = torrentDao.all()
        return if (entities.isEmpty()) {
            emptyList()
        } else {
            entities.map { it.toTask() }
        }
    }

    suspend fun insertTorrent(task: TorrentTask) {
        torrentDao.put(task.toEntity())
    }

    suspend fun deleteTorrent(hash: String): Int {
        return torrentDao.delete(hash)
    }

    suspend fun exitTorrent(hash: String): Boolean {
        return torrentDao.count(hash) > 0
    }

    suspend fun getTorrent(hash: String): TorrentTask? {
        return torrentDao.get(hash)?.toTask()
    }
}


private fun TorrentTask.toEntity(): TorrentEntity {
    return TorrentEntity(
        hash = hash,
        source = source,
        downloadPath = downloadPath,

        name = name,
        priorityList = priorityList,

        sequentialDownload = sequentialDownload,
        paused = paused,
        finished = finished,
        downloadingMetadata = downloadingMetadata,

        addedDate = addedDate,
        error = error
    )
}

private fun TorrentEntity.toTask(): TorrentTask {
    return TorrentTask(
        hash = hash,
        source = source,
        downloadPath = downloadPath,

        name = name,
        priorityList = priorityList,

        sequentialDownload = sequentialDownload,
        paused = paused,
        finished = finished,
        downloadingMetadata = downloadingMetadata,

        addedDate = addedDate,
        error = error
    )
}