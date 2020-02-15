package com.seiko.tv.domain.bangumi

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.util.toHomeImageBean
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiFavoriteFixedUseCase : KoinComponent {

    private val detailsRepo: BangumiDetailsRepository by inject()

    operator fun invoke(): List<HomeImageBean> {
        return detailsRepo.getBangumiDetailsListFixed().map { it.toHomeImageBean() }
    }

}