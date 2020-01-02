package com.seiko.data.local.db

import io.objectbox.BoxStore

class DbHelperImpl(boxStore: BoxStore) : DbHelper {

    private val torrentBox = boxStore.boxFor(TorrentEntity::class.java)

    override suspend fun getTorrentEntities(): List<TorrentEntity> {
        return torrentBox.all
    }

    override suspend fun insertTorrentEntity(entity: TorrentEntity) {
        torrentBox.put(entity)
    }

    override suspend fun updateTorrentEntity(entity: TorrentEntity) {
        if (entity.id == 0L) {
            torrentBox.query()
                .equal(TorrentEntity_.hash, entity.hash)
                .build()
                .findFirst()?.let {
                    entity.id = it.id
                }
        }
        torrentBox.put(entity)
    }

    override suspend fun deleteTorrentEntity(entity: TorrentEntity) {
        torrentBox.remove(entity.id)
    }

}