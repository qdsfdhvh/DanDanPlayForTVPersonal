package com.seiko.tv.domain.bangumi

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
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

    fun invoke(count: Int): LiveData<List<HomeImageBean>> {
        return historyRepo.getBangumiDetailsList(count).map { list ->
            list.map { it.toHomeImageBean() }
        }
    }

}