package com.seiko.data.local.db

interface DbHelper {

    suspend fun getTorrentEntities(): List<TorrentEntity>

    suspend fun insertTorrentEntity(entity: TorrentEntity)

    suspend fun updateTorrentEntity(entity: TorrentEntity)

    suspend fun deleteTorrentEntity(entity: TorrentEntity)

}