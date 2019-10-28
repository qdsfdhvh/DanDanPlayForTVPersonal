package com.seiko.data.utils

import android.content.Context
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.seiko.domain.entity.ThunderLocalUrl
//import com.xunlei.downloadlib.XLDownloadManager
//import com.xunlei.downloadlib.XLTaskHelper
//import com.xunlei.downloadlib.parameter.BtIndexSet
//import com.xunlei.downloadlib.parameter.BtTaskParam
//import com.xunlei.downloadlib.parameter.XLTaskLocalUrl
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class XLTaskHelperManager(private val context: Context) {

    private val atomicInteger = AtomicInteger(0)

    init {
        init()
    }

    private fun init() {
//        XLTaskHelper.init(context, 1)
    }

    fun getLocalUrl(checkedFilePosition: Int,
                    checkedFileSize: Long,
                    torrentFilePath: String): ThunderLocalUrl {
        atomicInteger.set(0)
        return getLocalUrl(checkedFilePosition, checkedFileSize, torrentFilePath, atomicInteger.get())
    }

    private fun getLocalUrl(checkedFilePosition: Int,
                    checkedFileSize: Long,
                    torrentFilePath: String,
                    position: Int): ThunderLocalUrl {

//        val thunderTorrentInfo = XLTaskHelper.getInstance().getTorrentInfo(torrentFilePath)
//        if (thunderTorrentInfo == null) {
//            return ThunderLocalUrl.error(Exception("播放失败，无法解析播放内容"))
//        }
//
//        val cacheFolder = File(DEFAULT_CACHE_FOLDER_PATH)
//        if (!cacheFolder.exists() && !cacheFolder.mkdirs()) {
//            return ThunderLocalUrl.error(Exception("播放失败，创建缓存文件夹失败"))
//        }
//        FileUtils.deleteAllInDir(cacheFolder)
//
//        if (cacheFolder.freeSpace < checkedFileSize) {
//            return ThunderLocalUrl.error(Exception("播放失败，剩余缓存空间不足"))
//        }
//
//        // 构建参数
//        val taskParam = BtTaskParam()
//        taskParam.setCreateMode(1)
//        taskParam.setFilePath(cacheFolder.absolutePath)
//        taskParam.setMaxConcurrent(3)
//        taskParam.setSeqId(position)
//        taskParam.setTorrentPath(torrentFilePath)
//
//        // 选择的文件
//        val selectIndexSet = BtIndexSet(1)
//        selectIndexSet.mIndexSet[0] = checkedFilePosition
//
//        // 忽略的文件
//        val size = thunderTorrentInfo.mSubFileInfo.size
//        val deSelectIndexSet = BtIndexSet(size - 1)
//        var j = 0
//        for (i in 0 until size) {
//            if (i != checkedFilePosition) {
//                deSelectIndexSet.mIndexSet[j++] = 0
//            }
//        }
//
//        // 开启任务
//        val playTaskId = XLTaskHelper.getInstance().startTask(taskParam, selectIndexSet, deSelectIndexSet)
//
//        // 任务出错重试
//        if (playTaskId == -1L) {
//            XLTaskHelper.getInstance().stopTask(playTaskId)
//            return if (position < 3) {
//                XLDownloadManager.getInstance().uninit()
//                init()
//                getLocalUrl(checkedFilePosition, checkedFileSize, torrentFilePath, atomicInteger.addAndGet(1))
//            } else {
//                FileUtils.deleteAllInDir(DEFAULT_CACHE_FOLDER_PATH)
//                return ThunderLocalUrl.error(Exception("播放失败，无法开始播放任务"))
//            }
//        }
//
//        val fileName = thunderTorrentInfo.mSubFileInfo[checkedFilePosition].mFileName
//        val filePath = taskParam.mFilePath + File.separator + fileName
//        val localUrl = XLTaskLocalUrl()
//        XLDownloadManager.getInstance().getLocalUrl(filePath, localUrl)
//        LogUtils.d("fileName = $fileName, filePath = $filePath, localUrl = ${localUrl.mStrUrl}")
//        return ThunderLocalUrl.success(playTaskId, fileName, localUrl.mStrUrl)
        return ThunderLocalUrl.error(null)
    }

}