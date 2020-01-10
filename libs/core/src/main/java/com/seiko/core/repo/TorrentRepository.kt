package com.seiko.core.repo

import com.seiko.core.data.db.model.TorrentEntity

interface TorrentRepository {

    suspend fun getTorrents(): List<TorrentEntity>

    suspend fun insertTorrent(entity: TorrentEntity)

    suspend fun deleteTorrent(hash: String)

    suspend fun exitTorrent(hash: String): Boolean

    suspend fun getTorrent(hash: String): TorrentEntity?

}