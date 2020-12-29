package com.seiko.tv.domain.bangumi

import androidx.paging.PagingData
import com.seiko.common.util.extensions.asFlow
import com.seiko.common.util.extensions.dataMap
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.util.toHomeImageBean
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取本地收藏动漫（PagedList）
 */
@ActivityRetainedScoped
class GetBangumiFavoriteUseCase @Inject constructor(
    private val detailsRepo: BangumiDetailsRepository
) {
    fun execute(count: Int = 0): Flow<PagingData<HomeImageBean>> {
        return detailsRepo.getBangumiDetailsList(count)
            .asFlow()
            .dataMap { it.toHomeImageBean() }
    }
}