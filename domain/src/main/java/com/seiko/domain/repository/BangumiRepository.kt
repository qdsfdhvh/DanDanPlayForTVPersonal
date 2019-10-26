package com.seiko.domain.repository

import com.seiko.domain.entity.BangumiDetails
import com.seiko.domain.entity.BangumiIntro
import com.seiko.domain.entity.BangumiSeason
import com.seiko.domain.utils.Result

interface BangumiRepository {

    /**
     * 获取新番列表
     * // @param filterAdultContent 是否过滤成人内容
     */
    suspend fun getBangumiList(): Result<List<BangumiIntro>>

    /**
     * 获取动画类型番剧季度的列表
     */
    suspend fun getBangumiSeasons(): Result<List<BangumiSeason>>

    /**
     * 获取指定季度中上映的动画番剧
     * @param season 某个新番季度，如：18年1月、19年7月
     * // @param filterAdultContent 是否过滤成人内容
     */
    suspend fun getBangumiListWithSeason(season: BangumiSeason): Result<List<BangumiIntro>>

    /**
     * 获取番剧详情
     * @param animeId 作品编号
     */
    suspend fun getBangumiDetails(animeId: Int): Result<BangumiDetails>

//    /**
//     * 获取用户近期关注但未看/未看完的番剧的列表。
//     */
//    suspend fun getBangumiQueueIntros(): Result<List<BangumiIntro>>
//
//    /**
//     * 获取用户完整的未看完的番剧的列表
//     */
//    suspend fun getBangumiQueueDetails(): Result<List<BangumiQueueDetails>>

}