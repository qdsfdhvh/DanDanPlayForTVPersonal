package com.dandanplay.tv.domain

import com.seiko.common.data.Result
import com.dandanplay.tv.data.db.model.BangumiDetailsEntity
import com.dandanplay.tv.repo.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 收藏or取消收藏 - 保存动漫详情内容or删除
 */
class SaveFavoriteBangumiDetailsUseCase : KoinComponent {

    private val bangumiRepository: BangumiRepository by inject()

    suspend operator fun invoke(details: BangumiDetailsEntity): Result<Boolean> {
        return if (details.isFavorited) {
            bangumiRepository.insertBangumiDetails(details)
        } else {
            bangumiRepository.deleteBangumiDetails(details.animeId)
        }
    }
}