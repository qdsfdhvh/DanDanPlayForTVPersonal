package com.seiko.core.data.api

import com.seiko.core.data.Result
import com.seiko.core.model.api.ResMagnetItem
import com.seiko.core.util.safeApiCall
import okio.IOException

internal class ResDanDanApiRemoteDataSource(private val api: ResDanDanApiService) {

    suspend fun searchMagnetList(keyword: String,
                                 typeId: Int,
                                 subGroupId: Int): Result<List<ResMagnetItem>> {
        return safeApiCall(
            call = { requestSearchMagnetList(keyword, typeId, subGroupId) },
            errorMessage = "Error searchMagnetList"
        )
    }

    private suspend fun requestSearchMagnetList(keyword: String,
                                                typeId: Int,
                                                subGroupId: Int): Result<List<ResMagnetItem>> {
        val type = if (typeId < 0) "" else typeId.toString()
        val subGroup = if (subGroupId < 0) "" else subGroupId.toString()

        val response = api.searchMagnetList(keyword, type, subGroup)
        return Result.Success(response.resources)
    }
}