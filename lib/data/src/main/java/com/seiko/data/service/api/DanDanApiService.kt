package com.seiko.data.service.api

import com.seiko.data.service.response.*
import retrofit2.http.*

internal interface DanDanApiService {

    /**
     * 获取官方的新番列表
     * // @param filterAdultContent 是否过滤成人内容 @Query("filterAdultContent") filterAdultContent: Boolean
     */
    @GET("api/v2/bangumi/shin")
    suspend fun getBangumiList(): BangumiListResponse


    /**
     * 获取动画类型番剧季度的列表
     */
    @GET("api/v2/bangumi/season/anime")
    suspend fun getBangumiSeasons(): BangumiSeasonListResponse

    /**
     * 获取指定季度中上映的动画番剧列表
     * @param year 年份
     * @param month 季度月份（一般指1、4、7、10）
     * // @param filterAdultContent 是否过滤成人内容 @Query("filterAdultContent") filterAdultContent: Boolean
     */
    @GET("api/v2/bangumi/season/anime/{year}/{month}")
    suspend fun getBangumiListWithSeason(@Path("year") year: Int,
                                         @Path("month") month: Int): BangumiListResponse

    /**
     * 获取指定编号的作品的详细数据，包括简介、评分、详细剧集等。
     * @param animeId 作品编号
     */
    @GET("api/v2/bangumi/{animeId}")
    suspend fun getBangumiDetails(@Path("animeId") animeId: Int): BangumiDetailsResponse

    /**
     * 获取用户近期关注但未看/未看完的番剧的列表。
     *
     * PS: 此接口需要登录状态才可调用。
     */
    @GET("api/v2/bangumi/queue/intro")
    suspend fun getBangumiQueueIntros(): BangumiQueueIntroResponse

    /**
     * 获取用户完整的未看完的番剧的列表。
     *
     * PS: 此接口需要登录状态才可调用。
     */
    @GET("api/v2/bangumi/queue/details")
    suspend fun getBangumiQueueDetails(): BangumiQueueDetailsResponse

    /**
     * 根据用户提供的关键词搜索到对应的作品信息，搜索结果中不包含剧集信息。
     * @param keyword 关键字
     * @param type 作品类型
     */
    @GET("/api/v2/search/anime")
    suspend fun searchBangumiList(@Query("keyword") keyword: String,
                                  @Query("type") type: String): SearchAnimeResponse

}