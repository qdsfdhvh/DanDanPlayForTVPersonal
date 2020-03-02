package com.seiko.tv.domain.bangumi

import com.seiko.common.data.Result
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.comments.DanDanApiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 获取动漫详情
 */
class GetBangumiDetailsUseCase : KoinComponent {

    private val repo: DanDanApiRepository by inject()
    private val detailsRepo: BangumiDetailsRepository by inject()

    suspend fun invoke(animeId: Long): Result<BangumiDetailsEntity> {
        return when(val result = repo.getBangumiDetails(animeId)) {
            is  Result.Success -> {
                val details = result.data
                // 本地是否已经收藏
                details.isFavorited = detailsRepo.isFavorited(details.animeId)
                return Result.Success(details)
            }
            is Result.Error -> result
        }
    }

}