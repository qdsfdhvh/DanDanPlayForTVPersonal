package com.seiko.data.service.api

import com.seiko.data.service.response.ResMagnetSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface ResDanDanApiService {

    /**
     * 根据关键字搜索磁力信息
     * @param keyword 关键字
     * @param typeId 作品类型id
     * @param subGroupId 字幕组id
     */
    @GET("/list")
    suspend fun searchMagnetList(
        @Query("keyword") keyword: String,
        @Query("type") typeId: String,
        @Query("subgroup") subGroupId: String): ResMagnetSearchResponse



}