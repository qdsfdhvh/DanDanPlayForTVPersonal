package com.seiko.tv.data.repo

import com.seiko.tv.data.comments.DanDanApiRemoteDataSource
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.data.model.api.SearchAnimeDetails
import com.seiko.common.data.Result
import com.seiko.tv.data.comments.ResDanDanApiRemoteDataSource

internal class SearchRepositoryImpl(
    private val dataSource: DanDanApiRemoteDataSource,
    private val resDataSource: ResDanDanApiRemoteDataSource
) : SearchRepository {

    override suspend fun searchBangumiList(keyword: String, type: String): Result<List<SearchAnimeDetails>> {
        return dataSource.searchBangumiList(keyword, type)
    }

    override suspend fun searchMagnetList(keyword: String, typeId: Int, subGroupId: Int): Result<List<ResMagnetItemEntity>> {
        return resDataSource.searchMagnetList(keyword, typeId, subGroupId)
    }

}