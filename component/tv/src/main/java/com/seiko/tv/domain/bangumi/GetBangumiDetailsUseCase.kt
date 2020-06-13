package com.seiko.tv.domain.bangumi

import com.seiko.common.data.Result
import com.seiko.tv.data.comments.BangumiDetailsRepository
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.comments.DanDanApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 获取动漫详情
 */
class GetBangumiDetailsUseCase : KoinComponent {

    private val repo: DanDanApiRepository by inject()
    private val detailsRepo: BangumiDetailsRepository by inject()
    private val saveBangumiHistory: SaveBangumiHistoryUseCase by inject()

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