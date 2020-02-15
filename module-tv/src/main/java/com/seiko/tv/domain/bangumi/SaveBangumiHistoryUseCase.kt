package com.seiko.tv.domain.bangumi

import com.seiko.common.data.Result
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.data.comments.BangumiHistoryRepository
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.db.model.BangumiHistoryEntity
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 收藏or取消收藏 - 保存动漫详情内容or删除
 */
class SaveBangumiHistoryUseCase : KoinComponent {

    private val historyRepo: BangumiHistoryRepository by inject()
    private val bangumiRepo: BangumiDetailsRepository by inject()

    suspend operator fun invoke(details: BangumiDetailsEntity): Result<Boolean> {
        // 如果是已收藏 收藏动漫，更新最后一次浏览时间
        bangumiRepo.updateBangumiDetailsUpdateDate(details.animeId)
        // 添加历史
        return  historyRepo.saveBangumiDetails(details)
    }

}