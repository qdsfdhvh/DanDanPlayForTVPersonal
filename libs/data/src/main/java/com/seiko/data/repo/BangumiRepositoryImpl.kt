package com.seiko.data.repo

import com.seiko.data.http.api.DanDanApiService
import com.seiko.data.model.response.BangumiDetailsResponse
import com.seiko.data.model.response.BangumiListResponse
import com.seiko.data.model.response.BangumiSeasonListResponse
import com.seiko.data.model.api.BangumiDetails
import com.seiko.data.model.api.BangumiIntro
import com.seiko.data.model.api.BangumiSeason
import com.seiko.data.utils.Result
import retrofit2.HttpException

internal class BangumiRepositoryImpl(private val api: DanDanApiService) :
    BangumiRepository {

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