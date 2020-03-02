package com.seiko.tv.domain.bangumi

import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.seiko.tv.data.comments.BangumiHistoryRepository
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.util.toHomeImageBean
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 获取本地动漫浏览历史（PagedList）
 */
class GetBangumiHistoryUseCase : KoinComponent {

    private val historyRepo: BangumiHistoryRepository by inject()

    private val pageSize = 10

    private val config = Config(
        pageSize = pageSize,
        prefetchDistance = pageSize / 5,
        enablePlaceholders = false,
        initialLoadSizeHint = pageSize,
        maxSize = pageSize * 2)

    fun invoke(count: Int): LiveData<PagedList<HomeImageBean>> {
        return historyRepo.getBangumiDetailsList(count)
            .map { it.toHomeImageBean() }
            .toLiveData(config)
    }

}