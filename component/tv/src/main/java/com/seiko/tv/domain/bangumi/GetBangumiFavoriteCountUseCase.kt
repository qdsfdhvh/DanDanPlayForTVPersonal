package com.seiko.tv.domain.bangumi

import com.seiko.tv.data.comments.BangumiDetailsRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 获取本地收藏动漫数量
 */
@ActivityRetainedScoped
class GetBangumiFavoriteCountUseCase @Inject constructor(
    private val detailsRepo: BangumiDetailsRepository
) {
    suspend fun execute(): Flow<Int> {
        return flow {
            emit(detailsRepo.count())
        }
    }
}