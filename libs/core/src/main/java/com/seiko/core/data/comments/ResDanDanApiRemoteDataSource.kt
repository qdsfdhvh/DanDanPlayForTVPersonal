package com.seiko.core.data.comments

import com.seiko.core.data.Result
import com.seiko.core.data.api.ResDanDanApiService
import com.seiko.core.data.db.model.ResMagnetItemEntity
import com.seiko.core.util.safeApiCall

internal class ResDanDanApiRemoteDataSource(private val api: ResDanDanApiService) {

    suspend fun searchMagnetList(keyword: String,
                                 typeId: Int,
                                 subGroupId: Int): Result<List<ResMagnetItemEntity>> {
        return safeApiCall(
            call = { requestSearchMagnetList(keyword, typeId, subGroupId) },
            errorMessage = "Error searchMagnetList"
        )
    }

    private suspend fun requestSearchMagnetList(keyword: String,
                                                typeId: Int,
                                                subGroupId: Int): Result<List<ResMagnetItemEntity>> {
        val type = if (typeId < 0) "" else typeId.toString()
        val subGroup = if (subGroupId < 0) "" else subGroupId.toString()

        val response = api.searchMagnetList(keyword, type, subGroup)
        return Result.Success(response.resources)
    }
}