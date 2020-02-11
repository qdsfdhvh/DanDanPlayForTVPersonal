package com.seiko.player.domain

import com.seiko.common.data.Result
import com.seiko.player.data.model.PlayParam
import com.seiko.player.util.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import timber.log.Timber
import java.io.File
import java.util.*

class GetSubtitleUserCase : KoinComponent {

    suspend operator fun invoke(param: PlayParam): Result<String> {
        return withContext(Dispatchers.IO) {
            val uri = param.videoUri
            val videoPath = if ("file".equals(uri.scheme, ignoreCase = true)) {
                uri.path
            } else {
                uri.toString()
            }
            val subtitles = getLocalSubtitlePath(videoPath)
            Timber.d(subtitles.toString())
            if (subtitles.isNullOrEmpty()) {
                return@withContext Result.Error(RuntimeException("Not Found support subtitle with: $videoPath"))
            }
            return@withContext Result.Success(subtitles[0])
        }
    }

}

/**
 * 支持的字幕格式集合
 */
private val SUPPORT_EXT_ARRAY = arrayOf("ass", "scc", "srt", "stl", "ttml")

/**
 * 获得与视频同目录的字幕集合
 */
private fun getLocalSubtitlePath(filePath: String?): List<String>? {
    if (filePath.isNullOrEmpty()) return null

    val file = File(filePath)
    if (!file.exists()) return null

    //  无后缀名称
    val videoName = FileUtil.getFileNotExt(file.absolutePath)

    return file.parentFile.listFiles().filter {
        val path = it.absolutePath
        var bool = path.startsWith(videoName)

        if (bool) {
            val ext = FileUtil.getFileExt(path)
            if (ext.isNotEmpty()) {
                bool = bool && SUPPORT_EXT_ARRAY.contains(ext.toLowerCase(Locale.US))
            }
        }
        bool
    }.map { it.absolutePath }
}
