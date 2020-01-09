package com.seiko.module.torrent.model

import com.seiko.torrent.TorrentDownloadTask
import com.seiko.torrent.constants.TorrentStateCode

data class DownloadProgress(
    var hash: String = "",
    var name: String = "",
    @TorrentStateCode
    var state: Int = TorrentStateCode.UNKNOWN,
    var progress: Int = 0,

    var receivedBytes: Long = 0,
    var uploadedBytes: Long = 0,
    var totalBytes: Long = 0,

    /**
     * 下载数据
     */
    var downloadSpeed: Long = 0,
    /**
     * 上传数据
     */
    var uploadSpeed: Long = 0,

    var eta: Long = -1,

    /**
     * 添加日期
     */
    var addedDate: Long = 0,

    /**
     * 总连接用户
     */
    var totalPeers: Int = 0,

    /**
     * 活跃用户
     */
    var peers: Int = 0,

    /**
     * 异常
     */
    var error: String = ""
) {
    constructor(downloadTask: TorrentDownloadTask) : this(
        hash = downloadTask.task.hash,
        name = downloadTask.task.name,
        state = downloadTask.stateCode,
        progress = downloadTask.progress,
        receivedBytes = downloadTask.receivedBytes,
        uploadedBytes = downloadTask.uploadedBytes,
        totalBytes = downloadTask.totalBytes,
        downloadSpeed = downloadTask.downloadSpeed,
        uploadSpeed = downloadTask.uploadSpeed,
        eta = downloadTask.eta,

        addedDate = downloadTask.task.addedDate,
        totalPeers = downloadTask.totalPeers,
        peers = downloadTask.connectedPeers,
        error = downloadTask.task.error
    )

    override fun toString(): String {
        return  "DownloadProgress{" +
                "hash=$hash" +
                ", name=$name" +
                ", state=$state" +
                ", progress=$progress" +
                ", receivedBytes=$receivedBytes" +
                ", uploadedBytes=$uploadedBytes" +
                ", totalBytes=$totalBytes" +
                ", downloadSpeed=$downloadSpeed" +
                ", uploadSpeed=$uploadSpeed" +
                ", eta=$eta" +
                ", addedDate=$addedDate" +
                ", totalPeers=$totalPeers" +
                ", peers=$peers" +
                ", error=$error" +
                "}"
    }
}