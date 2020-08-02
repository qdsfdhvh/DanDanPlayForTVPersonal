package com.seiko.tv.domain.bangumi

import com.seiko.tv.data.comments.BangumiHistoryRepository
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.util.toHomeImageBean
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 获取本地动漫浏览历史（List）
 */
@ActivityRetainedScoped
class GetBangumiHistoryFixedUseCase @Inject constructor(
    private val historyRepo: BangumiHistoryRepository
) {
    suspend fun invoke(count: Int): List<HomeImageBean> {
        return historyRepo.getBangumiDetailsListFixed(count)
            .map { it.toHomeImageBean() }
    }
}