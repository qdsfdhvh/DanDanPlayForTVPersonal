package com.seiko.data.local.db

import com.seiko.data.models.TorrentEntity

interface DbHelper {

    suspend fun getTorrents(): List<TorrentEntity>

    suspend fun insertTorrent(entity: TorrentEntity)

    suspend fun deleteTorrent(hash: String)

    suspend fun exitTorrent(hash: String): Boolean

    suspend fun getTorrent(hash: String): TorrentEntity?
}