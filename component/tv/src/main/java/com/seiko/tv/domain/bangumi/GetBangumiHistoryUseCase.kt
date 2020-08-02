package com.seiko.tv.domain.bangumi

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.seiko.tv.data.comments.BangumiHistoryRepository
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.util.toHomeImageBean
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 获取本地动漫浏览历史（PagedList）
 */
@ActivityRetainedScoped
class GetBangumiHistoryUseCase @Inject constructor(
    private val historyRepo: BangumiHistoryRepository
) {
    fun invoke(count: Int): LiveData<List<HomeImageBean>> {
        return historyRepo.getBangumiDetailsList(count).map { list ->
            list.map { it.toHomeImageBean() }
        }
    }
}