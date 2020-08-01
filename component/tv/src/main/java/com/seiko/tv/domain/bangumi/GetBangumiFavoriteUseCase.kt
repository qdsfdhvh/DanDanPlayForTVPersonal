package com.seiko.tv.domain.bangumi

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.util.toHomeImageBean
import org.koin.core.KoinComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 获取本地收藏动漫（PagedList）
 */
@Singleton
class GetBangumiFavoriteUseCase @Inject constructor(
    private val detailsRepo: BangumiDetailsRepository
) : KoinComponent {

    operator fun invoke(count: Int): LiveData<List<HomeImageBean>> {
        return detailsRepo.getBangumiDetailsList(count).map { list ->
            list.map { it.toHomeImageBean() }
        }
    }

}