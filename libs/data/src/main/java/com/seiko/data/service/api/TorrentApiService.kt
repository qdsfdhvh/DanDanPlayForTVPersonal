package com.seiko.data.service.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

internal interface TorrentApiService {

    /**
     * 下载种子
     */
    @POST("/Magnet/Parse")
    suspend fun downloadTorrent(@Body requestBody: RequestBody): ResponseBody

}