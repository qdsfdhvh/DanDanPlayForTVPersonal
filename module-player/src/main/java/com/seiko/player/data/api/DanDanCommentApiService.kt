package com.seiko.player.data.api

import com.seiko.player.data.model.DanmaDownloadBean
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

/**
 * 弹弹Play  弹幕接口
 */
interface DanDanCommentApiService {

    /**
     * 下载弹幕
     * @param episodeId 番剧集数id
     */
    @GET("api/v2/comment/{episodeId}")
    @Headers("accept-encoding: gzip")
    suspend fun downloadDanma(@Path("episodeId") episodeId: Int): DanmaDownloadBean

}