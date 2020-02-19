package com.seiko.player.domain.media

import android.content.Context
import android.provider.MediaStore
import com.seiko.common.util.getMD5
import com.seiko.player.data.comments.VideoMediaRepository
import com.seiko.player.data.db.model.VideoMedia
import com.seiko.player.util.FileUtils
import com.seiko.player.util.getFileNameFromPath
import com.seiko.player.util.getVideoMd5
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

/**
 * 检查本地视频
 */
class QueryVideoMediaUseCase : KoinComponent {

    private val videoMediaRepo: VideoMediaRepository by inject()
    private val completionVideoMedia: CompletionVideoMediaUseCase by inject()

    suspend operator fun invoke() {
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
                    
                    while (cursor.moveToNext()) {
                        videoPath =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))

                        videoMd5 = File(videoPath).getMD5()
                        id =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                        videoSize =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                        videoDuration =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))

                        list.add(
                            VideoMedia(
                                id = id,
                                videoMd5 = videoMd5,
                                videoPath = videoPath,
                                videoTitle = videoPath.getFileNameFromPath(),
                                videoSize = videoSize,
                                videoDuration = videoDuration
                            )
                        )
                    }

                    if (list.isNotEmpty()) {
                        // 保存视频
                        videoMediaRepo.saveMediaList(list)

                        // 完善视频信息(缩略图)
                        completionVideoMedia.invoke()
                    }
                }
            }
    }

}
