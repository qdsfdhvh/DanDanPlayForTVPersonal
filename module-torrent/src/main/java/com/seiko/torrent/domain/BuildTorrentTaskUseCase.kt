package com.seiko.torrent.domain

import com.seiko.common.data.Result
import com.seiko.download.torrent.model.TorrentMetaInfo
import com.seiko.torrent.data.model.AddTorrentParams
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
            source = source,
            fromMagnet = fromMagnet,
            sha1hash = info.sha1Hash,
            name = name,
            filePriorities = priorities,
            pathToDownload = downloadPath,
            sequentialDownload = isSequentialDownload,
            addPaused = !autoStart)
        return Result.Success(params)
    }
}