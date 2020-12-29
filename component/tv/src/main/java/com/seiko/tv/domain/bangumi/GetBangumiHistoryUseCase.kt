package com.seiko.tv.domain.bangumi

import com.seiko.common.util.extensions.asFlow
import com.seiko.common.util.extensions.dataMap
import com.seiko.tv.data.comments.BangumiHistoryRepository
import com.seiko.tv.util.toHomeImageBean
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 获取本地历史动漫 - page
 */
@ActivityRetainedScoped
class GetBangumiHistoryUseCase @Inject constructor(
    private val historyRepo: BangumiHistoryRepository
) {
    fun execute(count: Int) = historyRepo.getBangumiDetailsList(count)
        .asFlow()
        .dataMap { it.toHomeImageBean() }
}