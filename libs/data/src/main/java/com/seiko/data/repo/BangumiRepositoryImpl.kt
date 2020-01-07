package com.seiko.data.repo

import com.seiko.data.http.api.DanDanApiService
import com.seiko.data.http.response.BangumiDetailsResponse
import com.seiko.data.http.response.BangumiListResponse
import com.seiko.data.http.response.BangumiSeasonListResponse
import com.seiko.domain.model.BangumiDetails
import com.seiko.domain.model.BangumiIntro
import com.seiko.domain.model.BangumiSeason
import com.seiko.domain.repo.BangumiRepository
import com.seiko.domain.utils.Result
import retrofit2.HttpException

internal class BangumiRepositoryImpl(private val api: DanDanApiService) : BangumiRepository {

    override suspend fun getBangumiList(): Result<List<BangumiIntro>> {
        val response: BangumiListResponse
        try  {
            response = api.getBangumiList()
        } catch (e: HttpException) {
            return Result.Error(e)
        }
        if (response.success) {
            return Result.Success(response.bangumiList)
        }
        return Result.Error(Exception(response.errorMessage))
    }

    override suspend fun getBangumiSeasons(): Result<List<BangumiSeason>> {
        val response: BangumiSeasonListResponse
        try {
            response = api.getBangumiSeasons()
        } catch (e: HttpException) {
            return Result.Error(e)
        }
        if (response.success) {
            return Result.Success(response.seasons)
        }
        return Result.Error(Exception(response.errorMessage))
    }

    override suspend fun getBangumiListWithSeason(season: BangumiSeason): Result<List<BangumiIntro>> {
        val response: BangumiListResponse
        try  {
            response = api.getBangumiListWithSeason(season.year, season.month)
        } catch (e: HttpException) {
            return Result.Error(e)
        }
        if (response.success) {
            return Result.Success(response.bangumiList)
        }
        return Result.Error(Exception(response.errorMessage))
    }

    override suspend fun getBangumiDetails(animeId: Int): Result<BangumiDetails> {
        val response: BangumiDetailsResponse
        try  {
            response = api.getBangumiDetails(animeId)
        } catch (e: HttpException) {
            return Result.Error(e)
        }
        if (response.success) {
            return Result.Success(response.bangumi)
        }
        return Result.Error(Exception(response.errorMessage))
    }

}