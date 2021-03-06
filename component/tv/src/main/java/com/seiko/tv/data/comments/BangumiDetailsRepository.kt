package com.seiko.tv.data.comments

import com.seiko.tv.data.db.dao.BangumiDetailsDao
import com.seiko.tv.data.db.dao.BangumiEpisodeDao
import com.seiko.tv.data.db.dao.BangumiIntroDao
import com.seiko.tv.data.db.dao.BangumiTagDao
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.util.annotation.BangumiIntroType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BangumiDetailsRepository @Inject constructor(
    private val bangumiDetailsDao: BangumiDetailsDao,
    private val bangumiEpisodeDao: BangumiEpisodeDao,
    private val bangumiIntroDao: BangumiIntroDao,
    private val bangumiTagDao: BangumiTagDao
) {

    /**
     * 是否已经收藏了此动漫
     * @param animeId 动漫id
     */
    suspend fun isFavorited(animeId: Long): Boolean {
        return bangumiDetailsDao.count(animeId) > 0
    }

    /**
     * 本地收藏的动漫数量
     */
    suspend fun count(): Int {
        return bangumiDetailsDao.count()
    }

    /**
     * 获得本地收藏的全部动漫 - 分页
     */
    fun getBangumiDetailsList() = bangumiDetailsDao.all()

    /**
     * 获得本地收藏的前x不动漫 - liveData
     */
    fun getBangumiDetailsListLiveData(count: Int) = bangumiDetailsDao.allLiveData(count)

    /**
     * 尝试更新收藏的动漫的浏览时间
     */
    suspend fun updateBangumiDetailsUpdateDate(animeId: Long) {
        if (isFavorited(animeId)) {
            bangumiDetailsDao.update(animeId, System.currentTimeMillis())
        }
    }

    /**
     * 保存动漫
     */
    suspend fun saveBangumiDetails(details: BangumiDetailsEntity): Boolean {
        return withContext(Dispatchers.Default) {
            val current = System.currentTimeMillis()
            details.addedDate = current
            details.updateDate = current
            details.createDate = current
            bangumiDetailsDao.put(details)

            val animeId = details.animeId

            // 删除旧数据
            bangumiEpisodeDao.delete(animeId)
            bangumiIntroDao.delete(animeId)
            bangumiTagDao.delete(animeId)


            details.episodes.forEach { it.fromAnimeId = animeId }
            bangumiEpisodeDao.put(details.episodes)
            details.relateds.forEach {
                it.fromAnimeId = animeId
                it.fromType = BangumiIntroType.RELATED
            }
            bangumiIntroDao.put(details.relateds)
            details.similars.forEach {
                it.fromAnimeId = animeId
                it.fromType = BangumiIntroType.SIMILAR
            }
            bangumiIntroDao.put(details.similars)
            details.tags.forEach {
                it.fromAnimeId = animeId
            }
            bangumiTagDao.put(details.tags)
            true
        }
    }

    /**
     * 删除动漫
     */
    suspend fun removeBangumiDetails(animeId: Long): Boolean {
        val id = bangumiDetailsDao.delete(animeId)
        if (id > 0) {
            bangumiEpisodeDao.delete(animeId)
            bangumiIntroDao.delete(animeId)
            bangumiTagDao.delete(animeId)
        }
        return true
    }

}