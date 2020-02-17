package com.seiko.player.domain.media

import android.content.Context
import android.provider.MediaStore
import com.seiko.common.util.getMD5
import com.seiko.player.data.db.model.VideoMedia
import com.seiko.player.util.getVideoMd5
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

/**
 * 搜索全部视频
 */
class QueryVideoFormMediaStoreUseCase : KoinComponent {

    private val createVideoThumbnailPath: CreateVideoThumbnailPathUseCase by inject()

    operator fun invoke(): List<VideoMedia> {
        val context: Context by inject()
        val contentResolver = context.contentResolver
        contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
            .use { cursor ->
                if (cursor != null) {
                    val list = ArrayList<VideoMedia>()

                    var id: Long
                    var videoPath: String
                    var videoMd5: String
                    var videoSize: Long
                    var videoDuration: Long
                    var videoThumbnail: String
                    
                    while (cursor.moveToNext()) {
                        videoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))

                        videoMd5 = File(videoPath).getMD5()
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                        videoSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                        videoDuration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                        videoThumbnail = createVideoThumbnailPath.invoke(videoPath)

                        list.add(VideoMedia(
                            id = id,
                            videoMd5 = videoMd5,
                            videoPath = videoPath,
                            videoSize = videoSize,
                            videoDuration = videoDuration,
                            videoThumbnail =videoThumbnail))
                    }
                    return list
                }
            }
        return emptyList()
    }

}
