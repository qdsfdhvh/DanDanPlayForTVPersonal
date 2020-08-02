package com.seiko.tv.domain.bangumi

import com.seiko.common.data.Result
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 收藏or取消 本地动漫收藏
 */
@ActivityRetainedScoped
class SaveBangumiFavoriteUseCase @Inject constructor(
    private val bangumiRepo: BangumiDetailsRepository
) {
    suspend operator fun invoke(details: BangumiDetailsEntity): Result<Boolean> {
        val success = if (details.isFavorited) {
            bangumiRepo.saveBangumiDetails(details)
        } else {
            bangumiRepo.removeBangumiDetails(details.animeId)
        }
        return Result.Success(success)
    }
}