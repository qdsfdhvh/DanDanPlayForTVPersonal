package com.seiko.tv.data.comments

import androidx.paging.DataSource
import com.seiko.common.data.Result
import com.seiko.tv.data.db.AppDatabase
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.db.model.BangumiHistoryEntity

internal class BangumiHistoryRepository(database: AppDatabase) {

    private val bangumiHistoryDao = database.bangumiHistoryDao()

    /**
     * 获得本地动漫前x条浏览历史，按浏览时间排序
     * PS：插入新数据时，会动态变化
     * @param count 前多少条
     */
    fun getBangumiDetailsList(count: Int): DataSource.Factory<Int, BangumiHistoryEntity> {
        return bangumiHistoryDao.all(count)
    }

    /**
     * 获得本地动漫前x条浏览历史，按浏览时间排序
     * @param count 前多少条
     */
    fun getBangumiDetailsListFixed(count: Int): List<BangumiHistoryEntity> {
        return bangumiHistoryDao.allFixed(count)
    }

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