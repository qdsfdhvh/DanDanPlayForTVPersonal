package com.seiko.data.helper

import com.seiko.data.local.db.DbHelper
import com.seiko.data.models.TorrentEntity
import com.seiko.torrent.TorrentEngine
import com.seiko.torrent.models.MagnetInfo
import com.seiko.torrent.models.TorrentMetaInfo
import com.seiko.torrent.models.TorrentTask
import java.io.File

class TorrentHelper(
    private val torrentEngine: TorrentEngine,
    private val dbHelper: DbHelper
) {

    fun fetchMagnet(uri: String): MagnetInfo {
        val params = torrentEngine.fetchMagnet(uri)
        return MagnetInfo(
            uri = uri,
            sha1hash = params.infoHash().toHex(),
            name = params.name(),
            filePriorities = params.filePriorities()?.toList() ?: emptyList()
        )
    }

    suspend fun addTorrent(task: TorrentTask, isFromMagnet: Boolean, removeFile: Boolean): Boolean {
        when {
            isFromMagnet -> {
                // 尝试通过磁力查找种子数据
                val bencode = torrentEngine.getLoadedMagnet(task.hash)
                torrentEngine.removeLoadedMagnet(task.hash)

                if (bencode == null) {
                    // 没有种子数据，标记下载
                    task.downloadingMetadata = true
                    // 尝试记入数据库
                    if (!dbHelper.exitTorrent(task.hash)) {
                        dbHelper.insertTorrent(task.toEntity())
                    }
                } else {
                    // 已经下载种子数据，不需要下载
                    task.downloadingMetadata = false

                    // 将种子数据写入本地，如果本存在会删除旧数据重新写入
                    val newFile = torrentEngine.createTorrentFile(task.hash, bencode)
                    if (newFile != null) {
                        task.source = newFile.absolutePath
                    }
                    // 是否删除已经存在的目录
                    if (removeFile) {
                        File(task.source).deleteRecursively()
                    }
                    // 数据库存在此种子信息，尝试并入现有任务
                    if (dbHelper.exitTorrent(task.hash)) {
                        torrentEngine.mergeTorrent(task, bencode)
                    }
                    // 写入or更新 数据库种子信息
                    dbHelper.insertTorrent(task.toEntity())
                }
            }
            // 本地存在种子数据
            File(task.source).exists() -> {
                // 数据库存在此种子信息，尝试并入现有任务
                if (dbHelper.exitTorrent(task.hash)) {
                    torrentEngine.mergeTorrent(task)
                }
                // 是否删除已经存在的目录
                if (removeFile) {
                    File(task.source).deleteRecursively()
                }
                // 写入or更新 数据库种子信息
                dbHelper.insertTorrent(task.toEntity())
            }
            else -> return false
        }

        // 不下载种子信息的情况，下载任务列表为空
        if (!task.downloadingMetadata && task.priorityList.isNullOrEmpty()) {
            dbHelper.deleteTorrent(task.hash)
            return false
        }

        torrentEngine.download(task)
        return true
    }

    suspend fun deleteTorrent(hash: String) {
        dbHelper.deleteTorrent(hash)
    }

    suspend fun getTorrent(hash: String): TorrentTask? {
        return dbHelper.getTorrent(hash)?.toTask()
    }

    suspend fun updateTorrent(task: TorrentTask) {
        dbHelper.insertTorrent(task.toEntity())
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