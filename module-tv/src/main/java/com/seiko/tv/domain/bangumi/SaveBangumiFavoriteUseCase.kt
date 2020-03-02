package com.seiko.tv.domain.bangumi

import com.seiko.common.data.Result
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 收藏or取消 本地动漫收藏
 */
class SaveBangumiFavoriteUseCase : KoinComponent {

    private val bangumiRepo: BangumiDetailsRepository by inject()

    suspend operator fun invoke(details: BangumiDetailsEntity): Result<Boolean> {
        val success = if (details.isFavorited) {
            bangumiRepo.saveBangumiDetails(details)
        } else {
            bangumiRepo.removeBangumiDetails(details.animeId)
        }
        return Result.Success(success)
    }
}