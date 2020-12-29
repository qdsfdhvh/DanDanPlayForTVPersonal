package com.seiko.tv.domain.bangumi

import com.seiko.tv.data.comments.BangumiHistoryRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 获取本地历史动漫数量
 */
@ActivityRetainedScoped
class GetBangumiHistoryCountUseCase @Inject constructor(
    private val detailsRepo: BangumiHistoryRepository
) {
    suspend fun execute(max: Int): Flow<Int> {
        return flow {
            emit(detailsRepo.countOrMax(max))
        }
    }
}