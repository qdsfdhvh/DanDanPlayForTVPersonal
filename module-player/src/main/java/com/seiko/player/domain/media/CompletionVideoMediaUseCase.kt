package com.seiko.player.domain.media

import com.seiko.player.data.comments.VideoMediaRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 为本地视频添加缩略图
 */
class CompletionVideoMediaUseCase : KoinComponent {

    private val videoMediaRepo: VideoMediaRepository by inject()
    private val createVideoThumbnailPath: CreateVideoThumbnailPathUseCase by inject()

    suspend operator fun invoke(): Boolean {
        val mediaList = videoMediaRepo.getMedialListWithEmptyThumbnail()
        var videoThumbnailPath: String
        var success = true
        for (media in mediaList) {
            videoThumbnailPath = createVideoThumbnailPath.invoke(media.videoPath)
            success = videoMediaRepo.updateMediaThumbnail(media.id, videoThumbnailPath)
        }
        return success
    }

}