package com.dandanplay.tv.repo

import com.dandanplay.tv.data.db.model.ResMagnetItemEntity
import com.dandanplay.tv.model.api.SearchAnimeDetails
import com.seiko.common.data.Result
import com.dandanplay.tv.data.comments.ResDanDanApiRemoteDataSource

internal class SearchRepositoryImpl(
    private val dataSource: com.dandanplay.tv.data.comments.DanDanApiRemoteDataSource,
    private val resDataSource: ResDanDanApiRemoteDataSource
) : SearchRepository {

    override suspend fun searchBangumiList(keyword: String, type: String): Result<List<SearchAnimeDetails>> {
        return dataSource.searchBangumiList(keyword, type)
    }

    override suspend fun searchMagnetList(keyword: String, typeId: Int, subGroupId: Int): Result<List<ResMagnetItemEntity>> {
        return resDataSource.searchMagnetList(keyword, typeId, subGroupId)
    }

}