package com.dandanplay.tv.domain

import com.seiko.core.data.Result
import com.seiko.core.data.db.model.BangumiDetailsEntity
import com.seiko.core.data.db.model.ResMagnetItemEntity
import com.seiko.core.repo.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 保存磁力相关数据
 */
class SaveMagnetInfoUseCase : KoinComponent {

    private val bangumiRepository: BangumiRepository by inject()

    suspend operator fun invoke(item: ResMagnetItemEntity,
                                hash: String,
                                animeId: Long,
                                episodeId: Int
    ): Result<Boolean> {
        //  保存磁力数据
        var result = bangumiRepository.insertResMagnetItem(hash, item)
        if (result is Result.Error) return result
        // 将动漫id和集数与hash关联
        if (animeId >= 0) {
            result = bangumiRepository.insertEpisodeTorrent(animeId, episodeId, hash)
        }
        return result
    }
}