package com.seiko.tv.domain.bangumi

import androidx.lifecycle.map
import com.seiko.tv.data.comments.BangumiHistoryRepository
import com.seiko.tv.util.toHomeImageBean
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 获取本地历史动漫前x条
 */
@ActivityRetainedScoped
class GetBangumiHistoryLiveDataUseCase @Inject constructor(
    private val historyRepo: BangumiHistoryRepository
) {
    fun execute(count: Int) = historyRepo.getBangumiDetailsListLiveData(count)
        .map { list ->
            list.map { it.toHomeImageBean() }
        }
}