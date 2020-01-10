package com.seiko.core.data.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * 下载种子
 */
internal interface TorrentApiService {

    @POST("/Magnet/Parse")
    suspend fun downloadTorrent(@Body requestBody: RequestBody): ResponseBody

    @GET
    suspend fun downloadTorrent(@Url url: String): ResponseBody

}