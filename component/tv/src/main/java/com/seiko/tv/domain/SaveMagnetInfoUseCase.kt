package com.seiko.tv.domain

import com.seiko.common.data.Result
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.data.comments.EpisodeTorrentRepository
import com.seiko.tv.data.comments.ResMagnetItemRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 保存磁力相关数据
 */
class SaveMagnetInfoUseCase : KoinComponent {

    private val repo: ResMagnetItemRepository by inject()
    private val episodeTorrentRepo: EpisodeTorrentRepository by inject()

    suspend operator fun invoke(
        item: ResMagnetItemEntity,
        hash: String,
        animeId: Long,
        episodeId: Int
    ): Result<Boolean> {
        //  保存磁力数据
        var result = repo.saveResMagnetItem(hash, item)
        if (!result) return Result.Success(result)
        // 将动漫id和集数与hash关联
        if (animeId >= 0) {
            result = episodeTorrentRepo.saveEpisodeTorrent(animeId, episodeId, hash)
        }
        return Result.Success(result)
    }
}