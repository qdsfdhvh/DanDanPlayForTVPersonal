package com.seiko.tv.data.comments

import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.db.model.BangumiIntroEntity
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.common.data.Result
import com.seiko.tv.data.api.DanDanApiService
import com.seiko.tv.data.api.model.JsonResultResponse
import com.seiko.tv.data.api.model.ResMagnetSearchResponse
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.data.model.api.SearchAnimeDetails
import com.seiko.tv.util.apiCall
import com.seiko.tv.util.apiFlowCall
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DanDanApiRepository(
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
            key = "${API_KEY_BANGUMI_WITH_SEASON}_${season.year}_${season.month}",
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
     * 搜索对应的作品信息
     * PS: 1. 搜索结果中不包含剧集信息
     *     2. 关键词长度至少为2。
     *     3. 关键词中的空格将被认定为 AND 条件，其他字符将被作为原始字符去搜索。
     *     4. 可以通过中文、日文、罗马音、英文等条件对作品的别名进行搜索，繁体中文关键词将被统一为简体中文。
     * @param keyword 关键字
     * @param type 作品类型 ['', 'tvseries', 'tvspecial', 'ova', 'movie', 'musicvideo', 'web',
     *             'other', 'jpmovie', 'jpdrama', 'unknown']
     */
    suspend fun searchBangumiList(keyword: String, type: String): Result<List<SearchAnimeDetails>> {
        return apiCall(
            request = { api.searchBangumiList(keyword, type) },
            success = { response ->
                Result.Success(response.animes)
            }
        )
    }

    /**
     * 搜索磁力连接
     * @param keyword 关键字
     * @param typeId 作品类型， 不过滤输入-1
     * @param subGroupId 字幕组Id， 不过滤输入-1
     */
    suspend fun searchMagnetList(keyword: String, typeId: Int, subGroupId: Int): Result<List<ResMagnetItemEntity>> {
        val response: ResMagnetSearchResponse
        try {
            val type = if (typeId < 0) "" else typeId.toString()
            val subGroup = if (subGroupId < 0) "" else subGroupId.toString()
            response = api.searchMagnetList("http://res.acplay.net/list/", keyword, type, subGroup)
        } catch (e: Exception) {
            return Result.Error(e)
        }
        return Result.Success(response.resources)
    }

    /**
     * 优先加载本地数据，数据过时更新数据
     */
    private inline fun <reified T: JsonResultResponse, R: Any> apiDbLiveCall(
        key: String, timeOut: Long,
        noinline isEffectCache: (T) -> Boolean = { true },
        noinline request: suspend () -> T,
        noinline success: suspend (T) -> Result<R>
    ): Flow<Result<R>> {
        return apiFlowCall(
            loadCache =  { httpDbCache.getHttpResponse(key, T::class.java) },
            isEffectCache = isEffectCache,
            isUpdateLocalCache = { httpDbCache.isOutData(key, timeOut) },
            saveCache = { response ->
                coroutineScope {
                    launch {
                        httpDbCache.saveHttpResponse(key, response, T::class.java)
                    }
                }
            },
            request = request,
            success = success
        )
    }
}