package com.seiko.player.data.api

import com.seiko.player.data.api.model.DanmaDownloadResponse
import com.seiko.player.data.api.model.MatchRequest
import com.seiko.player.data.api.model.MatchResponse
import retrofit2.http.*

/**
 * 弹弹Play  弹幕接口
 */
interface DanDanApi {

    /**
     * 下载弹幕
     * @param episodeId 番剧集数id
     */
    @GET("api/v2/comment/{episodeId}")
    @Headers("accept-encoding: gzip")
    suspend fun downloadDanma(@Path("episodeId") episodeId: Int): DanmaDownloadResponse

    /**
     * 此接口用于当用户打开某视频文件时，可以通过文件名称、Hash等信息查找此视频可能对应的节目信息。
     * 此接口首先会使用Hash信息进行搜寻，如果有相应的记录，会返回“精确关联”的结果（即isMatched属性为true，此时列表中只包含一个搜索结果）。
     * 如果Hash信息匹配失败，则会继续通过文件名进行模糊搜寻。
     */
    @POST("api/v2/match")
    @Headers("Content-Type: application/json")
    suspend fun getVideoMatch(@Body params: MatchRequest): MatchResponse

}