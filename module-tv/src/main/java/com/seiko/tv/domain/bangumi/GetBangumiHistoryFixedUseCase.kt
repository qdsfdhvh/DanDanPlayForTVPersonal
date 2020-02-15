package com.seiko.tv.domain.bangumi

import androidx.paging.PagedList
import com.seiko.tv.data.comments.BangumiHistoryRepository
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.util.toHomeImageBean
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiHistoryFixedUseCase : KoinComponent {

    private val historyRepo: BangumiHistoryRepository by inject()

    fun invoke(count: Int): List<HomeImageBean> {
        return historyRepo.getBangumiDetailsListFixed(count)
            .map { it.toHomeImageBean() }
    }

}