package com.dandanplay.tv.data.comments

import com.seiko.common.data.Result
import com.dandanplay.tv.data.api.ResDanDanApiService
import com.dandanplay.tv.data.db.model.ResMagnetItemEntity
import com.seiko.common.util.safeApiCall

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