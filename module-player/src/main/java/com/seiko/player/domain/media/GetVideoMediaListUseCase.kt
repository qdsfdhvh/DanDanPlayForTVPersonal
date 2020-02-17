package com.seiko.player.domain.media

import com.seiko.common.data.Result
import com.seiko.player.data.comments.VideoMediaRepository
import com.seiko.player.data.db.model.VideoMedia
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class GetVideoMediaListUseCase : KoinComponent {

    private val videoMediaRepo: VideoMediaRepository by inject()
    private val queryVideoFormMediaStore: QueryVideoFormMediaStoreUseCase by inject()

    suspend operator fun invoke(force: Boolean): Result<List<VideoMedia>> {
        var mediaList: List<VideoMedia>

        var start = System.currentTimeMillis()
        if (!force) {
            mediaList = videoMediaRepo.getMediaList()
            if (mediaList.isNotEmpty()) {
                Timber.d("media from db, 耗时：${System.currentTimeMillis() - start}")
                return Result.Success(mediaList)
            }
        }

        start = System.currentTimeMillis()
        mediaList = queryVideoFormMediaStore.invoke()
        Timber.d("media from system, 耗时：${System.currentTimeMillis() - start}")
        if (mediaList.isNotEmpty()) {
            videoMediaRepo.saveMediaList(mediaList)
        }
        return Result.Success(mediaList)
    }

}