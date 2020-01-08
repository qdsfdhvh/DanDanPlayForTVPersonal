package com.seiko.data.helper

import android.util.Log
import com.seiko.data.model.TorrentEntity
import com.seiko.data.repo.TorrentRepository
import com.seiko.data.usecase.torrent.GetTorrentInfoFileUseCase
import com.seiko.domain.utils.Result
import com.seiko.torrent.TorrentEngine
import com.seiko.torrent.model.MagnetInfo
import com.seiko.torrent.model.TorrentTask
import java.io.File

class TorrentHelper(
    private val torrentEngine: TorrentEngine,
    private val torrentRepo: TorrentRepository,
    private val getTorrentInfoFileUseCase: GetTorrentInfoFileUseCase
) {
    companion object {
        private const val TAG = "TorrentHelper"
    }

    fun fetchMagnet(uri: String): MagnetInfo {
        val params = torrentEngine.fetchMagnet(uri)
        return MagnetInfo(
            uri = uri,
            sha1hash = params.infoHash().toHex(),
            name = params.name(),
            filePriorities = params.filePriorities().toList()
        )
    }

    suspend fun addTorrent(task: TorrentTask, isFromMagnet: Boolean, removeFile: Boolean): Boolean {
        Log.d(TAG, "下载种子：$task")

        when {
            isFromMagnet -> {
                // 尝试通过磁力查找种子数据
                val bencode = torrentEngine.getLoadedMagnet(task.hash)
                torrentEngine.removeLoadedMagnet(task.hash)

                if (bencode == null) {
                    // 没有种子数据，标记下载
                    task.downloadingMetadata = true
                    // 尝试记入数据库
                    if (!torrentRepo.exitTorrent(task.hash)) {
                        torrentRepo.insertTorrent(task.toEntity())
                    }
                } else {
                    // 已经下载种子数据，不需要下载
                    task.downloadingMetadata = false

                    // 种子数据写入本地，并修改来源
                    val result = getTorrentInfoFileUseCase.invoke(task.hash)
                    if (result !is Result.Success) {
                        return false
                    }
                    val torrentFile = result.data
                    Log.d(TAG, "种子路径：$torrentFile")

                    torrentFile.writeBytes(bencode)
                    task.source = torrentFile.absolutePath

                    // 写入or更新 数据库种子信息
                    torrentRepo.insertTorrent(task.toEntity())
                }
            }
            // 本地存在种子数据
            File(task.source).exists() -> {
                // 数据库存在此种子信息，尝试并入现有任务
                if (torrentRepo.exitTorrent(task.hash)) {
                    torrentEngine.mergeTorrent(task)
                }

                // 是否删除已经存在的目录
                if (removeFile) {
                    File(task.source).deleteRecursively()
                }

                // 写入or更新 数据库种子信息
                torrentRepo.insertTorrent(task.toEntity())
            }
            else -> return false
        }

        // 不下载种子信息的情况，下载任务列表为空
        if (!task.downloadingMetadata && task.priorityList.isNullOrEmpty()) {
            torrentRepo.deleteTorrent(task.hash)
            return false
        }

        torrentEngine.download(task)
        return true
    }

    suspend fun restoreDownloads() {
        val tasks = torrentRepo.getTorrents()
        if (tasks.isEmpty()) return

        val loadList = ArrayList<TorrentTask>(tasks.size)
        for (task in tasks) {
            if (!task.downloadingMetadata && !File(task.source).exists()) {
                Log.d(TAG, "Torrent doesn't exists: $task")
                deleteTorrent(task.hash)
            } else {
                loadList.add(task.toTask())
            }
        }
        torrentEngine.restoreDownloads(loadList)
    }

    suspend fun getTorrent(hash: String): TorrentTask? {
        return torrentRepo.getTorrent(hash)?.toTask()
    }

    suspend fun updateTorrent(task: TorrentTask) {
        torrentRepo.insertTorrent(task.toEntity())
    }

    suspend fun deleteTorrent(hash: String) {
        torrentRepo.deleteTorrent(hash)
    }

}

private fun TorrentTask.toEntity(): TorrentEntity {
    return TorrentEntity(
        hash = hash,
        source = source,
        downloadPath = downloadPath,

        name = name,
        priorityList = priorityList,

        sequentialDownload = sequentialDownload,
        paused = paused,
        finished = finished,
        downloadingMetadata = downloadingMetadata,

        addedDate = addedDate,
        error = error
    )
}

private fun TorrentEntity.toTask(): TorrentTask {
    return TorrentTask(
        hash = hash,
        source = source,
        downloadPath = downloadPath,

        name = name,
        priorityList = priorityList,

        sequentialDownload = sequentialDownload,
        paused = paused,
        finished = finished,
        downloadingMetadata = downloadingMetadata,

        addedDate = addedDate,
        error = error
    )
}