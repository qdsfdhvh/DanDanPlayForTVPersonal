package com.seiko.data.repository

import com.seiko.data.net.api.DanDanApiService
import com.seiko.data.net.api.ResDanDanApiService
import com.seiko.data.response.ResMagnetSearchResponse
import com.seiko.data.response.SearchAnimeResponse
import com.seiko.domain.entity.ResMagnetItem
import com.seiko.domain.entity.SearchAnimeDetails
import com.seiko.domain.repository.SearchRepository
import com.seiko.domain.utils.Result

internal class SearchRepositoryImpl(private val api: DanDanApiService,
                           private val resApi: ResDanDanApiService
) : SearchRepository {

    override suspend fun getBangumiListWithSearch(keyword: String,
                                                  type: String): Result<List<SearchAnimeDetails>> {
        val response: SearchAnimeResponse
        try {
            response = api.getBangumiListWithSearch(keyword, type)
        } catch (e: Exception) {
            return Result.Failure(e)
        }
        if (response.success) {
            return Result.Success(response.animes)
        }
        return Result.Failure(Exception(response.errorMessage))
    }

    override suspend fun getMagnetListWithSearch(keyword: String,
                                                 typeId: Int,
                                                 subGroupId: Int): Result<List<ResMagnetItem>> {
        val response: ResMagnetSearchResponse
        try {
            val type = if (typeId < 0) "" else typeId.toString()
            val subGroup = if (subGroupId < 0) "" else subGroupId.toString()
            response = resApi.getMagnetListWithSearch(keyword, type, subGroup)
        } catch (e: Exception) {
            return Result.Failure(e)
        }
        return Result.Success(response.resources)
    }
}