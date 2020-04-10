package com.seiko.player.domain.danma

import com.seiko.common.data.Result
import com.seiko.common.util.getMD5
import com.seiko.player.data.comments.VideoMatchRepository
import org.videolan.vlc.danma.DanmaResultBean
import com.seiko.player.data.model.PlayParam
import com.seiko.player.util.getVideoMd5
import jcifs.smb.SmbFile
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException

/**
 * 获取弹幕集合 & 此视频的弹幕偏移时间
 */
class GetDanmaResultWithFileUseCase : KoinComponent {

    private val getDanmaComments: GetDanmaCommentsUseCase by inject()
    private val getVideoEpisodeId: GetVideoEpisodeIdUseCase by inject()
    private val videoMatchRepo: VideoMatchRepository by inject()

    /**
     * @param videoFile 视频路径
     * @param isMatched 是否精确匹配
     */
    suspend fun file(videoFile: File, isMatched: Boolean): Result<DanmaResultBean> {
        // 视频是否存在
        if (!videoFile.exists()) {
            return Result.Error(FileNotFoundException("Not found file: $videoFile"))
        }

        // 获取视频Md5
        val videoMd5 = videoFile.getVideoMd5()
        Timber.d("file md5=$videoMd5")

        return getResult(videoMd5, isMatched)
    }

    /**
     * @param videoFile 视频路径
     * @param isMatched 是否精确匹配
     */
    suspend fun smbFile(videoFile: SmbFile, isMatched: Boolean): Result<DanmaResultBean> {

        // 视频是否存在
        try {
            if (!videoFile.exists()) {
                return Result.Error(FileNotFoundException("Not found smbFile: $videoFile"))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }

        // 获取视频Md5
        val videoMd5 = videoFile.getVideoMd5()
        Timber.d("smbFile md5=$videoMd5")

        return getResult(videoMd5, isMatched)
    }

    private suspend fun getResult(videoMd5: String, isMatched: Boolean): Result<DanmaResultBean> {
        // 弹幕集合
        val comments = when(val result = getDanmaComments.hash(videoMd5, isMatched)) {
            is Result.Success -> result.data
            is Result.Error -> return result
        }

        // 此视频相应的episodeId
        val episodeId = when(val result = getVideoEpisodeId.hash(videoMd5, isMatched)) {
            is Result.Success -> result.data
            is Result.Error -> 0
        }

        // 视频弹幕偏移时间
        val shift = videoMatchRepo.getVideoShift(videoMd5, episodeId)

        return Result.Success(
            DanmaResultBean(
                comments = comments,
                shift = shift
            )
        )
    }

//    @Deprecated("目前尚未匹配成功过。")
//    suspend fun name(fileName: String, isMatched: Boolean): Result<DanmaResultBean> {
//        // 弹幕集合
//        val comments = when(val result = getDanmaComments.name(fileName, isMatched)) {
//            is Result.Success -> result.data
//            is Result.Error -> return result
//        }
//
//        // 此视频相应的episodeId
//        val episodeId = when(val result = getVideoEpisodeId.name(fileName, isMatched)) {
//            is Result.Success -> result.data
//            is Result.Error -> 0
//        }
//
//        // 视频弹幕偏移时间
//        val shift = videoMatchRepo.getVideoShift(fileName.getMD5(), episodeId)
//
//        return Result.Success(
//            DanmaResultBean(
//                comments = comments,
//                shift = shift
//            )
//        )
//    }
}