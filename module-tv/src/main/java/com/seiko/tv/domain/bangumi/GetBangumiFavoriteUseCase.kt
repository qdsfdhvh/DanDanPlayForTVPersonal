package com.seiko.tv.domain.bangumi

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.util.toHomeImageBean
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiFavoriteUseCase : KoinComponent {

    private val detailsRepo: BangumiDetailsRepository by inject()

    operator fun invoke(): LiveData<PagedList<HomeImageBean>> {
        val config = PagedList.Config.Builder()
            .setPageSize(8)
//            .setInitialLoadSizeHint(10)
            .setEnablePlaceholders(false)
            .build()
        return detailsRepo.getBangumiDetailsList()
            .map { it.toHomeImageBean() }
            .toLiveData(config)
    }

}