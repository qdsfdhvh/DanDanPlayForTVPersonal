package com.seiko.tv.domain.bangumi

import com.seiko.common.util.extensions.asFlow
import com.seiko.common.util.extensions.dataMap
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.util.toHomeImageBean
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 获取本地收藏动漫 - page
 */
@ActivityRetainedScoped
class GetBangumiFavoriteUseCase @Inject constructor(
    private val detailsRepo: BangumiDetailsRepository
) {
    fun execute() = detailsRepo.getBangumiDetailsList()
        .asFlow()
        .dataMap { it.toHomeImageBean() }
}