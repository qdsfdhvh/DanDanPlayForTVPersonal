package com.seiko.tv.domain.bangumi

import com.seiko.common.data.Result
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.tv.data.comments.DanDanApiRepository
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.util.toHomeImageBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 获取某一季度的动漫列表
 */
class GetBangumiListWithSeasonUseCase : KoinComponent {

    private val repo: DanDanApiRepository by inject()

    suspend operator fun invoke(season: BangumiSeason): Result<List<HomeImageBean>> {
        return when(val result = repo.getBangumiListWithSeason(season)) {
            is Result.Error -> result
            is Result.Success -> {
                val list = withContext(Dispatchers.Default) {
                    result.data.map { it.toHomeImageBean() }
                }
                Result.Success(list)
            }
        }
    }

}