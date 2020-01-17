package com.seiko.core.repo

import com.seiko.core.data.db.model.ResMagnetItemEntity
import com.seiko.core.model.api.SearchAnimeDetails
import com.seiko.core.data.Result
import com.seiko.core.data.comments.DanDanApiRemoteDataSource
import com.seiko.core.data.comments.ResDanDanApiRemoteDataSource

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