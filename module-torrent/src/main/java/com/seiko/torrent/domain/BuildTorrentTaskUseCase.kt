package com.seiko.torrent.domain

import com.seiko.common.data.Result
import com.seiko.torrent.data.db.TorrentEntity
import com.seiko.torrent.data.model.torrent.TorrentMetaInfo
import com.seiko.torrent.data.model.torrent.AddTorrentParams
import org.koin.core.KoinComponent
import org.libtorrent4j.Priority

class BuildTorrentTaskUseCase : KoinComponent {

    fun invoke(
        source: String,
        fromMagnet: Boolean,
        info: TorrentMetaInfo,
        selectedIndexes: List<Int>?,
        name: String,
        downloadPath: String,
        isSequentialDownload: Boolean,
        autoStart: Boolean
    ): Result<AddTorrentParams> {
        val priorities: List<Priority> = when {
            selectedIndexes == null || info.fileCount == selectedIndexes.size -> {
                MutableList(info.fileCount) { Priority.DEFAULT }
            }
            else -> {
                 MutableList(info.fileCount) { i ->
                     if (selectedIndexes.contains(i)) {
                         Priority.DEFAULT
                     } else {
                         Priority.IGNORE
                     }
                 }
            }
        }

        val params = AddTorrentParams(
            entity = TorrentEntity(
                source = source,
                hash = info.sha1Hash,
                name = name,
                priorityList = priorities,
                downloadPath = downloadPath,
                sequentialDownload = isSequentialDownload,
                paused = !autoStart,
                addedDate = System.currentTimeMillis()
            ),
            fromMagnet = fromMagnet
        )
        return Result.Success(params)
    }
}