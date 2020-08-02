package com.seiko.player.domain

import com.seiko.common.data.Result
import com.seiko.player.util.getVideoMd5
import org.videolan.vlc.danma.DanmaResultBean
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class GetDanmaResultWithFileUseCase @Inject constructor(
    private val getResult: GetDanmaResultUseCase
) {

    /**
     * @param videoFile 视频路径
     * @param isMatched 是否精确匹配
     */
    suspend operator fun invoke(videoFile: File, isMatched: Boolean): Result<DanmaResultBean> {
        // 视频是否存在
        if (!videoFile.exists()) {
            return Result.Error(FileNotFoundException("Not found file: $videoFile"))
        }

        // 获取视频Md5
        val videoMd5 = videoFile.inputStream().getVideoMd5()

        return getResult.invoke(videoMd5, isMatched)
    }
}