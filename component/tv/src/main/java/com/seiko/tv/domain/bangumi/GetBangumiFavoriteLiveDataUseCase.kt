package com.seiko.tv.domain.bangumi

import androidx.lifecycle.map
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.util.toHomeImageBean
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 获取本地收藏前x部动漫
 */
@ActivityRetainedScoped
class GetBangumiFavoriteLiveDataUseCase @Inject constructor(
    private val detailsRepo: BangumiDetailsRepository
) {
    fun execute(count: Int) = detailsRepo.getBangumiDetailsListLiveData(count).map { list ->
        list.map { it.toHomeImageBean() }
    }
}