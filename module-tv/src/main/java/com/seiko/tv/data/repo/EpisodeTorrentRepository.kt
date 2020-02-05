package com.seiko.tv.data.repo

import com.seiko.tv.data.db.dao.EpisodeTorrentDao

class EpisodeTorrentRepository(private val episodeTorrentDao: EpisodeTorrentDao) {

    suspend fun findEpisodeId(hash: String): Int {
        return episodeTorrentDao.find(hash)?.episodeId ?: -1
    }

}