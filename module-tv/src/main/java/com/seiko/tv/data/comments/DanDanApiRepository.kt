package com.seiko.tv.data.comments

import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.db.model.BangumiIntroEntity
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.common.data.Result
import com.seiko.tv.data.api.DanDanApiService
import com.seiko.tv.data.api.model.JsonResultResponse
import com.seiko.tv.util.apiFlowCall
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal class DanDanApiRepository(
    private val api: DanDanApiService,
    private val httpDbCache: HttpDbCacheRepository
)  {
    companion object {
        private const val API_KEY_SERIES_BANGUMI_LIST = "API_KEY_SERIES_BANGUMI_LIST"
        private const val API_KEY_BANGUMI_SEASON = "API_KEY_BANGUMI_SEASON"
        private const val API_KEY_BANGUMI_WITH_SEASON = "API_KEY_BANGUMI_WITH_SEASON"
        private const val API_KEY_BANGUMI_DETAILS = "API_KEY_BANGUMI_DETAILS"

        private const val ONE_HOUR = 60 * 60 * 1000L
        private const val ONE_DAY = 24 * ONE_HOUR
        private const val ONE_WEEK = 7 * ONE_DAY
    }

    /**
     * 获取新番列表
     * // @param filterAdultContent 是否过滤成人内容
     */
    fun getSeriesBangumiList(): Flow<Result<List<BangumiIntroEntity>>> {
        return apiDbLiveCall(
            key = API_KEY_SERIES_BANGUMI_LIST,
            timeOut = ONE_HOUR,
            isEffectCache = { it.bangumiList.isNotEmpty() },
            request = { api.getBangumiList() },
            success = { response -> Result.Success(response.bangumiList) }
        )
    }

    /**
     * 获取动画类型番剧季度的列表
     */
    fun getBangumiSeasons(): Flow<Result<List<BangumiSeason>>> {
        return apiDbLiveCall(
            key = API_KEY_BANGUMI_SEASON,
            timeOut = ONE_WEEK,
            isEffectCache = { it.seasons.isNotEmpty() },
            request = { api.getBangumiSeasons() },
            success = { response -> Result.Success(response.seasons) }
        )
    }

    /**
     * 获取指定季度中上映的动画番剧
     * @param season 某个新番季度，如：18年1月、19年7月
     * // @param filterAdultContent 是否过滤成人内容
     */
    fun getBangumiListWithSeason(season: BangumiSeason): Flow<Result<List<BangumiIntroEntity>>> {
        return apiDbLiveCall(
            key = "${API_KEY_BANGUMI_WITH_SEASON}}_${season.year}_${season.month}",
            timeOut = ONE_DAY,
            isEffectCache = { it.bangumiList.isNotEmpty() },
            request = { api.getBangumiListWithSeason(season.year, season.month) },
            success = { response -> Result.Success(response.bangumiList) }
        )
    }

    /**
     * 获取番剧详情
     * @param animeId 作品编号
     */
    fun getBangumiDetails(animeId: Long): Flow<Result<BangumiDetailsEntity>> {
        return apiDbLiveCall(
            key = "${API_KEY_BANGUMI_DETAILS}_$animeId",
            timeOut = ONE_HOUR,
            request = { api.getBangumiDetails(animeId) },
            success = { response -> Result.Success(response.bangumi) }
        )
    }

    /**
     * 优先加载本地数据，数据过时更新数据
     */
    private inline fun <reified T: JsonResultResponse, R: Any> apiDbLiveCall(
        key: String, timeOut: Long,
        noinline isEffectCache: (T) -> Boolean = { true },
        noinline request: suspend () -> T,
        noinline success: (T) -> Result<R>
    ): Flow<Result<R>> {
        return apiFlowCall(
            loadCache =  { httpDbCache.getHttpResponse(key, T::class.java) },
            isEffectCache = isEffectCache,
            isUpdateLocalCache = { httpDbCache.isOutData(key, timeOut) },
            request = request,
            success = { response ->
                coroutineScope {
                    launch {
                        httpDbCache.saveHttpResponse(key, response, T::class.java)
                    }
                }
                success.invoke(response)
            }
        )
    }
}