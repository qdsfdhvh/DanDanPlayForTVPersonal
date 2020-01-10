package com.seiko.core.repo

import com.seiko.core.data.Result
import com.seiko.core.data.api.DanDanApiRemoteDataSource
import com.seiko.core.model.api.BangumiDetails
import com.seiko.core.model.api.BangumiIntro
import com.seiko.core.model.api.BangumiSeason

internal class BangumiRepositoryImpl(
    private val dataSource: DanDanApiRemoteDataSource
) : BangumiRepository {

    override suspend fun getBangumiList(): Result<List<BangumiIntro>> {
        return dataSource.getBangumiList()
    }

    override suspend fun getBangumiSeasons(): Result<List<BangumiSeason>> {
        return dataSource.getBangumiSeasons()
    }

    override suspend fun getBangumiListWithSeason(season: BangumiSeason): Result<List<BangumiIntro>> {
        return dataSource.getBangumiListWithSeason(season)
    }

    override suspend fun getBangumiDetails(animeId: Int): Result<BangumiDetails> {
        return dataSource.getBangumiDetails(animeId)
    }

}