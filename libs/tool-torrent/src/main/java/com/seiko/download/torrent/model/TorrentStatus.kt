package com.seiko.download.torrent.model

import com.seiko.download.torrent.annotation.TorrentStateCode
import com.seiko.download.torrent.extensions.*
import org.libtorrent4j.TorrentHandle

/**
 * 种子下载状态
 */
class TorrentStatus(
    val hash: String,
    val title: String,
    val downloadPath: String,
    val dateAdded: Long,
    val error: String,

    @TorrentStateCode val state: Int,
    var downloadRate: Long = 0,
    var uploadRate: Long = 0,
    var progress: Float = 0f,
    var receivedBytes: Long = 0,
    var uploadedBytes: Long = 0,
    var totalBytes: Long = 0,
    var connectPeers: Int = 0,
    var totalPeers: Int = 0,
    var connectedSeeds: Int = 0,
    var totalSeeds: Int = 0,
    var eta: Long = 0
) {

    companion object {
        fun createInstance(
            task: TorrentTask,
            torrentHandle: TorrentHandle
        ) : TorrentStatus = TorrentStatus(
            hash = task.hash,
            title = task.name,
            downloadPath = task.downloadPath,
            dateAdded = task.addedDate,
            error = task.error,
            state = torrentHandle.getStateCode(),

            downloadRate = torrentHandle.getDownloadSpeed(),
            uploadRate = torrentHandle.getUploadSpeed(),

            progress = torrentHandle.getProgress(),
//            receivedBytes =  torrentHandle.getTotalReceivedBytes(),
            receivedBytes = torrentHandle.getTotalDone(),
            uploadedBytes = torrentHandle.getTotalSendBytes(),
            totalBytes = torrentHandle.getTotalWanted(),

            connectedSeeds = torrentHandle.getConnectSeeds(),
            totalSeeds = torrentHandle.getTotalSeeds(),

            connectPeers = torrentHandle.getConnectedPeers(),
            totalPeers = torrentHandle.getTotalPeers(),

            eta = torrentHandle.getETA()
        )
    }

    override fun toString(): String = "State: $state" +
//            ", Bencode Size: ${bencode.size}" +

            ", Download Rate: $downloadRate" +
            ", Upload Rate: $uploadRate" +

            ", Progress: $receivedBytes/$totalBytes ($progress)" +
            ", Peers: $connectPeers/$totalPeers" +
            ", Seeds: $connectedSeeds/$totalSeeds"
//            ", Magnet Uri: $magnetUri" +
//            ", Save Location: $saveLocationUri" +
//            ", Video File: $videoFileUri"
}