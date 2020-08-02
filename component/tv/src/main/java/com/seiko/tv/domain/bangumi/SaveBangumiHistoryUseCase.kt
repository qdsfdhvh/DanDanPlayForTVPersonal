package com.seiko.tv.domain.bangumi

import com.seiko.common.data.Result
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.data.comments.BangumiHistoryRepository
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 保存动漫到本地历史
 */
@ActivityRetainedScoped
class SaveBangumiHistoryUseCase @Inject constructor(
    private val historyRepo: BangumiHistoryRepository,
    private val bangumiRepo: BangumiDetailsRepository
) {
    suspend operator fun invoke(details: BangumiDetailsEntity): Result<Boolean> {
        // 如果是本地收藏动漫，更新最后浏览时间
        bangumiRepo.updateBangumiDetailsUpdateDate(details.animeId)
        // 添加历史
        return historyRepo.saveBangumiDetails(details)
    }
}