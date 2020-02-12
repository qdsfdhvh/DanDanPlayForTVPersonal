package com.seiko.tv.data.comments

import com.seiko.tv.data.db.AppDatabase
import com.seiko.tv.data.db.dao.EpisodeTorrentDao
import com.seiko.tv.data.db.model.EpisodeTorrentEntity

internal class EpisodeTorrentRepository(database: AppDatabase) {

    private val episodeTorrentDao = database.episodeTorrentDao()

    /**
     * 通过种子hash查找集数id，并不准确
     * @param hash 种子hash
     * @return 集数id
     */
    suspend fun findEpisodeId(hash: String): Int {
        return episodeTorrentDao.find(hash)?.episodeId ?: -1
    }

    /**
     * 保存 种子与 动漫||集数 关联信息
     * @param animeId 动画id 这个应该有
     * @param episodeId 集数id 不需要填-1
     * @param hash 种子hash
     * @return success
     */
    suspend fun saveEpisodeTorrent(animeId: Long, episodeId: Int, hash: String): Boolean {
        if (hash.isEmpty()) return false

        // 已添加
        if (episodeTorrentDao.count(animeId, episodeId, hash) > 0) {
            return false
        }

        episodeTorrentDao.put(
            EpisodeTorrentEntity(
                animeId = animeId,
                episodeId = episodeId,
                hash = hash
            )
        )
        return true
    }
}