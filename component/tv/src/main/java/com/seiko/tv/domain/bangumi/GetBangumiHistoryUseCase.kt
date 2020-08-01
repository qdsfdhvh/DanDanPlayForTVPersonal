package com.seiko.tv.domain.bangumi

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.seiko.tv.data.comments.BangumiHistoryRepository
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.util.toHomeImageBean
import org.koin.core.KoinComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 获取本地动漫浏览历史（PagedList）
 */
@Singleton
class GetBangumiHistoryUseCase @Inject constructor(
    private val historyRepo: BangumiHistoryRepository
) : KoinComponent {
    fun invoke(count: Int): LiveData<List<HomeImageBean>> {
        return historyRepo.getBangumiDetailsList(count).map { list ->
            list.map { it.toHomeImageBean() }
        }
    }
}