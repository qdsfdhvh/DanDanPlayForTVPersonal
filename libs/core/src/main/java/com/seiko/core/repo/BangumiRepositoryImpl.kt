package com.seiko.core.repo

import com.seiko.core.constants.BangumiIntroType
import com.seiko.core.data.Result
import com.seiko.core.data.api.DanDanApiRemoteDataSource
import com.seiko.core.data.db.AppDatabase
import com.seiko.core.data.db.model.BangumiDetailsEntity
import com.seiko.core.data.db.model.BangumiIntroEntity
import com.seiko.core.data.db.model.EpisodeTorrentEntity
import com.seiko.core.data.db.model.ResMagnetItemEntity
import com.seiko.core.model.api.BangumiSeason
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class BangumiRepositoryImpl(
    private val dataSource: DanDanApiRemoteDataSource,
    database: AppDatabase
) : BangumiRepository {

    private val bangumiDetailsDao by lazy { database.bangumiDetailsDao() }
    private val bangumiEpisodeDao by lazy { database.bangumiEpisodeDao() }
    private val bangumiIntroDao by lazy { database.bangumiIntroDao() }
    private val bangumiTagDao by lazy { database.bangumiTagDao() }
    private val resMagnetItemDao by lazy { database.resMagnetItemDao() }
    private val episodeTorrentDao by lazy { database.episodeTorrentDao() }


    override suspend fun getBangumiList(): Result<List<BangumiIntroEntity>> {
        return dataSource.getBangumiList()
    }

    override suspend fun getBangumiSeasons(): Result<List<BangumiSeason>> {
        return dataSource.getBangumiSeasons()
    }

    override suspend fun getBangumiListWithSeason(season: BangumiSeason): Result<List<BangumiIntroEntity>> {
        return dataSource.getBangumiListWithSeason(season)
    }

    override suspend fun getBangumiDetails(animeId: Long): Result<BangumiDetailsEntity> {
        val result = dataSource.getBangumiDetails(animeId)
        if (result is Result.Success) {
            val details = result.data
            details.isFavorited = bangumiDetailsDao.count(details.animeId) > 0
            val episodes = result.data.episodes
            if (episodes.isNotEmpty()) {
                episodes.forEach { episode ->
                    episode.airDate
                }
            }
            return Result.Success(details)
        }
        return result
    }

    override suspend fun getBangumiDetailsList(): Result<List<BangumiDetailsEntity>> {
        return Result.Success(bangumiDetailsDao.all())
    }

    override suspend fun insertBangumiDetails(details: BangumiDetailsEntity): Result<Boolean> {
        return withContext(Dispatchers.Default) {
            details.addedDate = System.currentTimeMillis()
            bangumiDetailsDao.put(details)

            val animeId = details.animeId

            // 删除旧数据
            bangumiEpisodeDao.delete(animeId)
            bangumiIntroDao.delete(animeId)
            bangumiTagDao.delete(animeId)


            details.episodes.forEach { it.fromAnimeId = animeId }
            bangumiEpisodeDao.put(details.episodes)
            details.relateds.forEach {
                it.fromAnimeId = animeId
                it.fromType = BangumiIntroType.RELATED
            }
            bangumiIntroDao.put(details.relateds)
            details.similars.forEach {
                it.fromAnimeId = animeId
                it.fromType = BangumiIntroType.SIMILAR
            }
            bangumiIntroDao.put(details.similars)
            details.tags.forEach {
                it.fromAnimeId = animeId
            }
            bangumiTagDao.put(details.tags)
            Result.Success(true)
        }
    }

    override suspend fun deleteBangumiDetails(animeId: Long): Result<Boolean> {
        bangumiDetailsDao.delete(animeId)
        bangumiEpisodeDao.delete(animeId)
        bangumiIntroDao.delete(animeId)
        bangumiTagDao.delete(animeId)
        return Result.Success(true)
    }

    override suspend fun insertResMagnetItem(hash: String, item: ResMagnetItemEntity): Result<Boolean> {
        item.hash = hash
        item.addedDate = System.currentTimeMillis()
        resMagnetItemDao.put(item)
        return Result.Success(true)
    }

    override suspend fun deleteResMagnetItem(hash: String): Result<Boolean> {
        resMagnetItemDao.delete(hash)
        return Result.Success(true)
    }

    override suspend fun insertEpisodeTorrent(animeId: Long, episodeId: Int, hash: String): Result<Boolean> {
        if (hash.isEmpty()) return Result.Error(RuntimeException("Hash is null."))

        // 已添加
        if (episodeTorrentDao.count(animeId, episodeId, hash) > 0) {
            return Result.Success(false)
        }

        episodeTorrentDao.put(EpisodeTorrentEntity(
            animeId = animeId,
            episodeId = episodeId,
            hash = hash
        ))
        return Result.Success(true)
    }
}