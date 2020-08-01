package com.seiko.tv.domain.bangumi

import com.seiko.tv.data.comments.BangumiHistoryRepository
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.util.toHomeImageBean
import org.koin.core.KoinComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 获取本地动漫浏览历史（List）
 */
@Singleton
class GetBangumiHistoryFixedUseCase @Inject constructor(
    private val historyRepo: BangumiHistoryRepository
) : KoinComponent {
    suspend fun invoke(count: Int): List<HomeImageBean> {
        return historyRepo.getBangumiDetailsListFixed(count)
            .map { it.toHomeImageBean() }
    }
}