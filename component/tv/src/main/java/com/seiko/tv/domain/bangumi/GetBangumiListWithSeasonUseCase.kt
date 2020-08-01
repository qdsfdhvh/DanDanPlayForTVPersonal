package com.seiko.tv.domain.bangumi

import com.seiko.common.data.Result
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.tv.data.comments.DanDanApiRepository
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.util.toHomeImageBean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.KoinComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 获取某一季度的动漫列表
 */
@Singleton
class GetBangumiListWithSeasonUseCase @Inject constructor(
    private val repo: DanDanApiRepository
) : KoinComponent {
    operator fun invoke(season: BangumiSeason): Flow<Result<List<HomeImageBean>>> {
        return repo.getBangumiListWithSeason(season).map { result ->
            when(result) {
                is Result.Success -> {
                    Result.Success(result.data.map { it.toHomeImageBean() })
                }
                is Result.Error -> result
            }
        }
    }
}