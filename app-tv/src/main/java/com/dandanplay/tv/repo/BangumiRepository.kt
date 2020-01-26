package com.dandanplay.tv.repo

import com.dandanplay.tv.data.db.model.BangumiDetailsEntity
import com.dandanplay.tv.data.db.model.BangumiIntroEntity
import com.dandanplay.tv.data.model.api.BangumiSeason
import com.seiko.common.data.Result
import com.dandanplay.tv.data.db.model.ResMagnetItemEntity

interface BangumiRepository {

    /**
     * 获取新番列表
     * // @param filterAdultContent 是否过滤成人内容
     */
    suspend fun getBangumiList(): Result<List<BangumiIntroEntity>>

    /**
     * 获取动画类型番剧季度的列表
     */
    suspend fun getBangumiSeasons(): Result<List<BangumiSeason>>

    /**
     * 获取指定季度中上映的动画番剧
     * @param season 某个新番季度，如：18年1月、19年7月
     * // @param filterAdultContent 是否过滤成人内容
     */
    suspend fun getBangumiListWithSeason(season: BangumiSeason): Result<List<BangumiIntroEntity>>

    /**
     * 获取番剧详情
     * @param animeId 作品编号
     */
    suspend fun getBangumiDetails(animeId: Long): Result<BangumiDetailsEntity>

//    /**
//     * 获取用户近期关注但未看/未看完的番剧的列表。
//     */
//    suspend fun getBangumiQueueIntros(): Result<List<BangumiIntro>>
//
//    /**
//     * 获取用户完整的未看完的番剧的列表
//     */
//    suspend fun getBangumiQueueDetails(): Result<List<BangumiQueueDetails>>

    /**
     * 获得本地所有动漫详情
     */
    suspend fun getBangumiDetailsList(): Result<List<BangumiDetailsEntity>>

    /**
     * 保存番剧详情
     */
    suspend fun insertBangumiDetails(details: BangumiDetailsEntity): Result<Boolean>

    /**
     * 删除番剧详情
     */
    suspend fun deleteBangumiDetails(animeId: Long): Result<Boolean>

    /**
     * 写入Magnet信息
     * @param hash 此magnet的hash，管理表需要用到，以参数传入
     * @param item Magnet信息
     */
    suspend fun insertResMagnetItem(hash: String, item: ResMagnetItemEntity): Result<Boolean>

    /**
     * 删除Magnet信息
     */
    suspend fun deleteResMagnetItem(hash: String): Result<Boolean>

    /**
     * 保存指定动漫指定集数的hash
     * @param animeId 动画id 这个应该有
     * @param episodeId 集数id 不需要填-1
     * @param hash 种子hash
     */
    suspend fun insertEpisodeTorrent(animeId: Long, episodeId: Int, hash: String): Result<Boolean>

}