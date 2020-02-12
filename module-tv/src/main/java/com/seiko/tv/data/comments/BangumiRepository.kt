package com.seiko.tv.data.comments

import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.db.model.BangumiIntroEntity
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.common.data.Result
import com.seiko.tv.data.api.DanDanApiService
import com.seiko.tv.data.api.model.BangumiDetailsResponse
import com.seiko.tv.data.api.model.BangumiListResponse
import com.seiko.tv.data.api.model.BangumiSeasonListResponse

internal class BangumiRepository(
    private val api: DanDanApiService
)  {

    /**
     * 获取新番列表
     * // @param filterAdultContent 是否过滤成人内容
     */
    suspend fun getBangumiList(): Result<List<BangumiIntroEntity>> {
        val response: BangumiListResponse
        try {
            response = api.getBangumiList()
        } catch (e: Exception) {
            return Result.Error(e)
        }

        if (!response.success) {
            return Result.Error(Exception("${response.errorCode} ${response.errorMessage}"))
        }
        return Result.Success(response.bangumiList)
    }

    /**
     * 获取动画类型番剧季度的列表
     */
    suspend fun getBangumiSeasons(): Result<List<BangumiSeason>> {
        val response: BangumiSeasonListResponse
        try {
            response = api.getBangumiSeasons()
        } catch (e: Exception) {
            return Result.Error(e)
        }
        if (!response.success) {
            return Result.Error(Exception("${response.errorCode} ${response.errorMessage}"))
        }
        return Result.Success(response.seasons)
    }

    /**
     * 获取指定季度中上映的动画番剧
     * @param season 某个新番季度，如：18年1月、19年7月
     * // @param filterAdultContent 是否过滤成人内容
     */
    suspend fun getBangumiListWithSeason(season: BangumiSeason): Result<List<BangumiIntroEntity>> {
        val response: BangumiListResponse
        try {
            response = api.getBangumiListWithSeason(season.year, season.month)
        } catch (e: Exception) {
            return Result.Error(e)
        }

        if (!response.success) {
            return Result.Error(Exception("${response.errorCode} ${response.errorMessage}"))
        }
        return Result.Success(response.bangumiList)
    }

    /**
     * 获取番剧详情
     * @param animeId 作品编号
     */
    suspend fun getBangumiDetails(animeId: Long): Result<BangumiDetailsEntity> {
        val response: BangumiDetailsResponse
        try {
            response =  api.getBangumiDetails(animeId)
        } catch (e: Exception) {
            return Result.Error(e)
        }
        if (!response.success) {
            return Result.Error(Exception("${response.errorCode} ${response.errorMessage}"))
        }
        return Result.Success(response.bangumi)
    }

}