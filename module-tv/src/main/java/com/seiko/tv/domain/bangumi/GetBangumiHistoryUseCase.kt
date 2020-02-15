package com.seiko.tv.domain.bangumi

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.seiko.tv.data.comments.BangumiHistoryRepository
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.util.toHomeImageBean
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiHistoryUseCase : KoinComponent {

    private val historyRepo: BangumiHistoryRepository by inject()

    fun invoke(): LiveData<PagedList<HomeImageBean>> {
        val config = PagedList.Config.Builder()
            .setPageSize(8)
            .setEnablePlaceholders(false)
            .build()
        return historyRepo.getBangumiDetailsList()
            .map { it.toHomeImageBean() }
            .toLiveData(config)
    }

}