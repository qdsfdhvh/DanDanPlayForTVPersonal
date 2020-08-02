package com.seiko.tv.domain.bangumi

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.util.toHomeImageBean
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 获取本地收藏动漫（PagedList）
 */
@ActivityRetainedScoped
class GetBangumiFavoriteUseCase @Inject constructor(
    private val detailsRepo: BangumiDetailsRepository
) {
    operator fun invoke(count: Int): LiveData<List<HomeImageBean>> {
        return detailsRepo.getBangumiDetailsList(count).map { list ->
            list.map { it.toHomeImageBean() }
        }
    }
}