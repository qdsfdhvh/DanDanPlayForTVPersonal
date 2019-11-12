package com.seiko.data.usecase

import com.blankj.utilcode.util.FileUtils
import com.seiko.data.utils.DEFAULT_CACHE_FOLDER_PATH
import com.seiko.domain.entity.ThunderLocalUrl
import com.seiko.domain.utils.Result
import com.xunlei.downloadlib.XLDownloadManager
import com.xunlei.downloadlib.XLTaskHelper
import com.xunlei.downloadlib.parameter.BtIndexSet
import com.xunlei.downloadlib.parameter.BtTaskParam
import com.xunlei.downloadlib.parameter.XLTaskLocalUrl
import org.koin.core.KoinComponent
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class GetTorrentLocalPlayUrlUseCase : KoinComponent {


    private val seq = AtomicInteger(0)

    /**
     * @param torrentFilePath 种子路径
     * @param checkedFilePosition 所选文件位置
     * @param checkedFileSize 所选文件大小
     */
    operator fun invoke(torrentFilePath: String,
                        checkedFilePosition: Int,
                        checkedFileSize: Long): Result<ThunderLocalUrl> {

        val thunderTorrentInfo = XLTaskHelper.getInstance().getTorrentInfo(torrentFilePath)
            ?: return Result.Error(Exception("播放失败，无法解析播放内容"))

        val cacheFolder = File(DEFAULT_CACHE_FOLDER_PATH)
        if (!cacheFolder.exists() && !cacheFolder.mkdirs()) {
            return Result.Error(Exception("播放失败，创建缓存文件夹失败"))
        }
        FileUtils.deleteAllInDir(cacheFolder)

        if (cacheFolder.freeSpace < checkedFileSize) {
            return Result.Error(Exception("播放失败，剩余缓存空间不足"))
        }

        // 构建参数
        val taskParam = BtTaskParam()
        taskParam.setCreateMode(1)
        taskParam.setFilePath(cacheFolder.absolutePath)
        taskParam.setMaxConcurrent(3)
        taskParam.setSeqId(seq.incrementAndGet())
        taskParam.setTorrentPath(torrentFilePath)

        // 选择的文件
        val selectIndexSet = BtIndexSet(1)
        selectIndexSet.mIndexSet[0] = checkedFilePosition

        // 忽略的文件
        val size = thunderTorrentInfo.mSubFileInfo.size
        val deSelectIndexSet = BtIndexSet(size - 1)
        var j = 0
        for (i in 0 until size) {
            if (i != checkedFilePosition) {
                deSelectIndexSet.mIndexSet[j++] = 0
            }
        }

        // 开启任务
        val playTaskId = XLTaskHelper.getInstance().startTask(taskParam, selectIndexSet, deSelectIndexSet)
        if (playTaskId == -1L) {
            return Result.Error(Exception("播放失败，无法开始播放任务"))
        }

        val fileName = thunderTorrentInfo.mSubFileInfo[checkedFilePosition].mFileName
        val filePath = taskParam.mFilePath + File.separator + fileName
        val localUrl = XLTaskLocalUrl()
        XLDownloadManager.getInstance().getLocalUrl(filePath, localUrl)

        return Result.Success(ThunderLocalUrl(
            taskId = playTaskId,
            title = fileName,
            url = localUrl.mStrUrl
        ))
    }
}