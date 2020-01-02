package com.seiko.data.repository

import com.seiko.data.service.api.DanDanApiService
import com.seiko.data.service.api.ResDanDanApiService
import com.seiko.data.service.response.ResMagnetSearchResponse
import com.seiko.data.service.response.SearchAnimeResponse
import com.seiko.domain.entity.ResMagnetItem
import com.seiko.domain.entity.SearchAnimeDetails
import com.seiko.domain.repository.SearchRepository
import com.seiko.domain.utils.Result

internal class SearchRepositoryImpl(
    private val api: DanDanApiService,
    private val resApi: ResDanDanApiService
) : SearchRepository {

    override suspend fun searchBangumiList(keyword: String,
                                           type: String): Result<List<SearchAnimeDetails>> {
        val response: SearchAnimeResponse
        try {
            response = api.searchBangumiList(keyword, type)
        } catch (e: Exception) {
            return Result.Error(e)
        }
        if (response.success) {
            return Result.Success(response.animes)
        }
        return Result.Error(Exception(response.errorMessage))
    }

    override suspend fun searchMagnetList(keyword: String,
                                          typeId: Int,
                                          subGroupId: Int): Result<List<ResMagnetItem>> {
        val response: ResMagnetSearchResponse
        try {
            val type = if (typeId < 0) "" else typeId.toString()
            val subGroup = if (subGroupId < 0) "" else subGroupId.toString()
            response = resApi.searchMagnetList(keyword, type, subGroup)
        } catch (e: Exception) {
            return Result.Error(e)
        }
        return Result.Success(response.resources)
    }

}