package com.seiko.tv.domain.bangumi

import com.seiko.common.data.Result
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.comments.DanDanApiRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 获取动漫详情
 */
@ActivityRetainedScoped
class GetBangumiDetailsUseCase @Inject constructor(
    private val repo: DanDanApiRepository,
    private val detailsRepo: BangumiDetailsRepository,
    private val saveBangumiHistory: SaveBangumiHistoryUseCase
) {
    fun invoke(animeId: Long): Flow<Result<BangumiDetailsEntity>> {
        return repo.getBangumiDetails(animeId).map { result ->
            when(result) {
                is  Result.Success -> {
                    val details = result.data
                    // 本地是否已经收藏
                    details.isFavorited = detailsRepo.isFavorited(details.animeId)
                    // 保留到浏览历史
                    coroutineScope {
                        launch(Dispatchers.IO) {
                            saveBangumiHistory.invoke(details)
                        }
                    }
                    Result.Success(details)
                }
                is Result.Error -> result
            }
        }
    }
}