package com.seiko.tv.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.paging.*
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.common.data.Result
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.data.comments.BangumiRepository
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.util.toHomeImageBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetFavoriteBangumiListUseCase : KoinComponent {

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