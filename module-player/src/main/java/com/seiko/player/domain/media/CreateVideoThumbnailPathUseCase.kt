package com.seiko.player.domain.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import com.seiko.player.util.constants.PLAYER_THUMBNAIL_DIR
import com.seiko.player.util.getVideoMd5
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * 创建视频缩略图
 */
class CreateVideoThumbnailPathUseCase : KoinComponent{

    private val videoThumbnailDir: File by inject(named(PLAYER_THUMBNAIL_DIR))

    operator fun invoke(videoPath: String): String {
        // 创建缩略图存储路径
        if (!videoThumbnailDir.exists() && !videoThumbnailDir.mkdirs()) {
            return ""
        }

        // 判断视频路径是否存在
        val videoFile = File(videoPath)
        if (!videoFile.exists()) return ""

        // 生成视频md5
        val videoMd5 = videoFile.getVideoMd5()

        // 创建缩略图路径，如果存在此文件则删除
        val videoThumbnailFile = File(videoThumbnailDir, "$videoMd5.jpg")
        if (videoThumbnailFile.exists() && !videoThumbnailFile.delete()) {
            return  ""
        }

        // 创建缩略图
        val bitmap = ThumbnailUtils.createVideoThumbnail(videoPath,
            MediaStore.Video.Thumbnails.MINI_KIND)

        // 保存缩略图并返回路径
        return try {
            videoThumbnailFile.writeBitmap(bitmap)
            videoThumbnailFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }
}

@Throws(IOException::class)
private fun File.writeBitmap(bitmap: Bitmap?) {
    if (bitmap == null) return
    val stream = FileOutputStream(this)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    stream.flush()
    stream.close()
    bitmap.recycle()
}