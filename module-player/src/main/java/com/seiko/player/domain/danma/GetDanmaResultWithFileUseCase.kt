package com.seiko.player.domain.danma

import com.seiko.common.data.Result
import com.seiko.player.util.getVideoMd5
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.videolan.vlc.danma.DanmaResultBean
import java.io.File
import java.io.FileNotFoundException

class GetDanmaResultWithFileUseCase : KoinComponent {

    private val getResult: GetDanmaResultUseCase by inject()

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
        val videoMd5 = videoFile.getVideoMd5()

        return getResult.invoke(videoMd5, isMatched)
    }
}