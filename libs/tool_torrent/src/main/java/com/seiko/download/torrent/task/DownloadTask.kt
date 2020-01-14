package com.seiko.download.torrent.task

import android.util.Log
import com.seiko.download.torrent.TorrentEngine
import com.seiko.download.torrent.constants.SAVE_RESUME_SYNC_TIME
import com.seiko.download.torrent.exception.FreeSpaceException
import com.seiko.download.torrent.extensions.getErrorMsg
import com.seiko.download.torrent.extensions.isPaused
import com.seiko.download.torrent.model.MagnetInfo
import com.seiko.download.torrent.model.TorrentMetaInfo
import com.seiko.download.torrent.model.TorrentSessionStatus
import com.seiko.download.torrent.model.TorrentTask
import com.seiko.download.torrent.utils.*
import org.libtorrent4j.*
import org.libtorrent4j.alerts.*
import java.io.File
import java.io.FileNotFoundException
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

private const val DOWNLOAD_TASK_TAG = "TorrentDownload"

/**
 * 待移除
 */
class DownloadTask(
    val torrentHandle: TorrentHandle,
    var task: TorrentTask
) {

    private var lastSaveResumeTime = 0L
    private val parts = File(task.downloadPath, ".${torrentHandle.infoHash()}.parts")
    private var autoManaged = true

    /**
     * 暂停任务
     */
    fun pause() {
        if (!torrentHandle.isValid) {
            return
        }

        torrentHandle.unsetFlags(TorrentFlags.AUTO_MANAGED)
        torrentHandle.pause()

        saveResumeData(true)
    }

    /**
     * 重启任务
     */
    fun resume() {
        if (!torrentHandle.isValid) {
            return
        }

        if (autoManaged) {
            torrentHandle.setFlags(TorrentFlags.AUTO_MANAGED)
        } else {
            torrentHandle.unsetFlags(TorrentFlags.AUTO_MANAGED)
        }
        torrentHandle.resume()

        saveResumeData(true)
    }

    /**
     * 是否自动管理
     */
    fun setAutoManaged(autoManaged: Boolean) {
        this.autoManaged = autoManaged
    }

    /**
     * 保存任务数据
     */
    fun saveResumeData(force: Boolean) {
        val now = System.currentTimeMillis()

        if (force || (now - lastSaveResumeTime) >= SAVE_RESUME_SYNC_TIME) {
            lastSaveResumeTime = now
        } else {
            /* Skip, too fast, see SAVE_RESUME_SYNC_TIME */
            return
        }

        try {
            if (torrentHandle.isValid) {
                torrentHandle.saveResumeData(TorrentHandle.SAVE_INFO_DICT)
            }
        } catch (e: Exception) {
            log(DOWNLOAD_TASK_TAG, "Error triggering resume data of $task:")
            log(DOWNLOAD_TASK_TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * 是否下载元数据，即种子信息
     */
    fun setSequentialDownload(sequentialDownload: Boolean) {
        if (sequentialDownload) {
            torrentHandle.setFlags(TorrentFlags.SEQUENTIAL_DOWNLOAD)
        } else {
            torrentHandle.unsetFlags(TorrentFlags.SEQUENTIAL_DOWNLOAD)
        }
    }

    fun addTrackers(trackers: List<AnnounceEntry>) {
        for (tracker in trackers) {
            torrentHandle.addTracker(tracker)
        }
        saveResumeData(true)
    }

    fun addWebSeeds(webSeeds: List<WebSeedEntry>) {
        for (webSeed in webSeeds) {
            when(webSeed.type()) {
                WebSeedEntry.Type.HTTP_SEED -> {
                    torrentHandle.addHttpSeed(webSeed.url())
                }
                WebSeedEntry.Type.URL_SEED -> {
                    torrentHandle.addUrlSeed(webSeed.url())
                }
                else -> {}
            }
        }
    }

    fun prioritizeFiles(priorities: Array<Priority>?) {
        if (!torrentHandle.isValid) return

        val info = torrentHandle.torrentFile() ?: return

        if (priorities != null) {
            if (info.numFiles() != priorities.size) return
            torrentHandle.prioritizeFiles(priorities)
        } else {
            torrentHandle.prioritizeFiles(Array(info.numFiles()) { Priority.DEFAULT })
        }
    }

    /****************************************************************
     *                            Status                            *
     ****************************************************************/

    val isPaused: Boolean get() = torrentHandle.status(true).isPaused()

    val status: TorrentSessionStatus get() = TorrentSessionStatus.createInstance(task, torrentHandle)

    val torrentInfo: TorrentInfo? get() = torrentHandle.torrentFile()

    /**
     * 删除无用文件
     */
    private fun finalCleanup(incompleteFiles: Set<File>?) {
        if (incompleteFiles == null) {
            return
        }

        for (file in incompleteFiles) {
            try {
                if (file.exists() && !file.delete()) {
                    log(DOWNLOAD_TASK_TAG, "Can't delete file $file")
                }
            } catch (e: Exception) {
                log(DOWNLOAD_TASK_TAG, "Can't delete file $file, ex: ${e.message}")
            }
        }
    }

    internal fun onTorrentRemoved() {
        parts.delete()
        finalCleanup(getIncompleteFile())
    }

    /**
     * 获取种子目录下的无用文件
     */
    private fun getIncompleteFile(): Set<File>? {
        if (task.downloadingMetadata) {
            return null
        }

        if (!torrentHandle.isValid) {
            return null
        }

        val set = HashSet<File>()

        try {
            val torrentFile = File(task.source)
            if (!torrentFile.exists()) {
                return null
            }
            val createdTime = torrentFile.lastModified()

            val info = torrentHandle.torrentFile()
            val fileStorage = info.files()
            val fileDir = task.downloadPath

            val progress = torrentHandle.fileProgress(TorrentHandle.FileProgressFlags.PIECE_GRANULARITY)
            for (i in progress.indices) {
                val filePath = fileStorage.filePath(i)
                val fileSize = fileStorage.fileSize(i)

                if (progress[i] < fileSize) {
                    val file = File(fileDir, filePath)
                    if (!file.exists()) {
                        continue
                    }

                    if (file.lastModified() >= createdTime) {
                        set.add(file)
                    }
                }
            }
        } catch (e: Exception) {
            log(DOWNLOAD_TASK_TAG, "Error calculating the incomplete files set of ${task.hash}")
        }
        return set
    }

}
