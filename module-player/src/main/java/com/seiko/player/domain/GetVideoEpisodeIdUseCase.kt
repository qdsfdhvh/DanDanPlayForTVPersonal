package com.seiko.player.domain

import com.seiko.common.data.Result
import com.seiko.player.data.api.model.MatchRequest
import com.seiko.player.data.comments.DanDanApiRepository
import com.seiko.player.data.comments.VideoMatchRepository
import com.seiko.player.data.model.PlayParam
import com.seiko.player.util.getVideoMd5
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.io.FileNotFoundException

class GetVideoEpisodeIdUseCase : KoinComponent {

    private val danmaApiRepo: DanDanApiRepository by inject()
    private val workMatchRepo: VideoMatchRepository by inject()

    /**
     * @param param 视频参数
     * @param isMatched 是否精确匹配
     */
    suspend operator fun invoke(param: PlayParam, isMatched: Boolean): Result<Int> {
        val videoPath = param.videoPath
        val videoFile = File(videoPath)

        // 视频是否存在
        if (!videoFile.exists()) {
            return Result.Error(FileNotFoundException("Not found file: $videoPath"))
        }

        // 获取视频Md5
        val videoMd5 = videoFile.getVideoMd5()

        // 尝试从数据库中获取
        val episodeList = workMatchRepo.getEpisodeIdList(videoMd5, isMatched)
        if (episodeList.isNotEmpty()) {
            return Result.Success(episodeList[0])
        }

        // 通过弹弹api查询与之绑定的动漫信息
        val request = MatchRequest.hash(videoMd5)
        return when (val result = danmaApiRepo.getVideoMatchList(request)) {
            is Result.Success -> {
                val matched = result.data.first
                val matchList = result.data.second
                // 将结果存入数据，不管是否精确关联
                workMatchRepo.saveMatchResult(videoMd5, matchList)

                if (isMatched == matched) {
                    Result.Success(matchList[0].episodeId)
                } else {
                    Result.Error(Exception("DanDanApi is not match this video:$videoPath"))
                }
            }
            is Result.Error -> result
        }
    }

}