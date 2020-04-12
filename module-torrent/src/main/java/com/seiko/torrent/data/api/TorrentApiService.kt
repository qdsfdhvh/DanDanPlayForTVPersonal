package com.seiko.torrent.data.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * 下载种子
 */
internal interface TorrentApiService {

    @POST("/Magnet/Parse")
    @Streaming
    suspend fun downloadTorrent(@Body requestBody: RequestBody): ResponseBody

    @GET
    @Streaming
    suspend fun downloadTorrent(@Url url: String): ResponseBody

}