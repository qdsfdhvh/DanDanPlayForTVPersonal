package com.dandanplay.tv.domain

import com.seiko.core.data.Result
import com.seiko.core.data.db.model.BangumiDetailsEntity
import com.seiko.core.repo.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 收藏or取消收藏 - 保存动漫详情内容or删除
 */
class FavoriteBangumiDetailsUseCase : KoinComponent {

    private val bangumiRepository: BangumiRepository by inject()

    suspend operator fun invoke(details: BangumiDetailsEntity): Result<Boolean> {
        return if (details.isFavorited) {
            bangumiRepository.insertBangumiDetails(details)
        } else {
            bangumiRepository.deleteBangumiDetails(details.animeId)
        }
    }
}