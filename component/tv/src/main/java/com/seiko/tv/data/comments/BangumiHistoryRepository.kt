package com.seiko.tv.data.comments

import com.seiko.common.data.Result
import com.seiko.tv.data.db.dao.BangumiHistoryDao
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.db.model.BangumiHistoryEntity
import javax.inject.Inject

class BangumiHistoryRepository @Inject constructor(
    private val bangumiHistoryDao: BangumiHistoryDao
) {

    /**
     * 本地浏览的动漫数量
     */
    suspend fun countOrMax(max: Int): Int = bangumiHistoryDao.countOrMax(max)

    /**
     * 获得本地动漫前x条浏览历史，按浏览时间排序 - 分页
     * @param count 前多少条
     */
    fun getBangumiDetailsList(count: Int) = bangumiHistoryDao.all(count)

    /**
     * 获得本地动漫前x条浏览历史，按浏览时间排序 - liveData
     * @param count 前多少条
     */
    fun getBangumiDetailsListLiveData(count: Int) = bangumiHistoryDao.allLiveData(count)

    suspend fun saveBangumiDetails(details: BangumiDetailsEntity): Result<Boolean> {
        val isExit = bangumiHistoryDao.count(details.animeId) > 0

        val current = System.currentTimeMillis()
        if (isExit) {
            bangumiHistoryDao.update(details.animeId, current)
        } else {
            bangumiHistoryDao.put(BangumiHistoryEntity(
                animeId = details.animeId,
                animeTitle = details.animeTitle,
                imageUrl = details.imageUrl,
                type = details.type,
                typeDescription = details.typeDescription,
                summary = details.summary,
                bangumiUrl = details.bangumiUrl,
                isOnAir = details.isOnAir,
                airDay = details.airDay,
                searchKeyword = details.searchKeyword,
                isRestricted = details.isRestricted,
                rating = details.rating,
                updateDate = current,
                createDate = current
            ))
        }
        return Result.Success(true)
    }

}