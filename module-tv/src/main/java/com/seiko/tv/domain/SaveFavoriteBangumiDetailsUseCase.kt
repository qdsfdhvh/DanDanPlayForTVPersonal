package com.seiko.tv.domain

import com.seiko.common.data.Result
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.comments.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 收藏or取消收藏 - 保存动漫详情内容or删除
 */
class SaveFavoriteBangumiDetailsUseCase : KoinComponent {

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