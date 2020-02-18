package com.seiko.player.domain.media

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.seiko.common.data.Result
import com.seiko.player.data.comments.VideoMediaRepository
import com.seiko.player.data.db.model.VideoMedia
import com.seiko.player.data.model.VideoBean
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class GetVideoMediaListUseCase : KoinComponent {

    private val videoMediaRepo: VideoMediaRepository by inject()

    operator fun invoke(): LiveData<PagedList<VideoBean>> {
        val config = PagedList.Config.Builder()
            .setPageSize(8)
            .setEnablePlaceholders(false)
            .build()
        return videoMediaRepo.getMediaList()
            .map { media ->
                VideoBean(
                    videoId = media.id,
                    videoPath = media.videoPath,
                    videoTitle = media.videoTitle,
                    videoThumbnail = media.videoThumbnail
                )
            }
            .toLiveData(config)
    }

}