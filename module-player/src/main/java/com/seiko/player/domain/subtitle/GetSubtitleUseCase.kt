package com.seiko.player.domain.subtitle

import com.seiko.common.data.Result
import com.seiko.player.data.model.PlayParam
import com.seiko.player.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import timber.log.Timber
import java.io.File
import java.util.*

class GetSubtitleUseCase : KoinComponent {

    suspend operator fun invoke(param: PlayParam): Result<List<String>> {
        return withContext(Dispatchers.IO) {
            val videoPath = param.videoPath
            val subtitles = getLocalSubtitlePath(
                videoPath
            ) ?: emptyList()
            Timber.d(subtitles.toString())
            return@withContext Result.Success(subtitles)
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

    // 去除后缀
    val videoName = FileUtils.getFileNotExt(file.absolutePath)

    return file.parentFile?.listFiles()?.filter {
        val path = it.absolutePath
        var bool = path.startsWith(videoName)

        if (bool) {
            val ext = FileUtils.getFileExt(path)
            if (ext.isNotEmpty()) {
                bool = bool && SUPPORT_EXT_ARRAY.contains(ext.toLowerCase(Locale.US))
            }
        }
        bool
    }?.map { it.absolutePath }
}
