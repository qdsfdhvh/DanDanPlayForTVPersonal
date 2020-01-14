package com.seiko.download.torrent.model

import com.seiko.download.torrent.constants.TorrentStateCode
import com.seiko.download.torrent.extensions.*
import org.libtorrent4j.TorrentHandle

@Suppress("unused", "MemberVisibilityCanBePrivate")
class TorrentSessionStatus private constructor(
    val hash: String,
    val error: String,

    @TorrentStateCode val state: Int,
    val downloadRate: Long,
    val uploadRate: Long,
    val progress: Float,
    val receivedBytes: Long,
    val uploadedBytes: Long,
    val totalBytes: Long,
    val connectPeers: Int,
    val totalPeers: Int,
    val connectedSeeds: Int,
    val totalSeeds: Int,
    val eta: Long
) {

    internal companion object {
        fun createInstance(
            task: TorrentTask,
            torrentHandle: TorrentHandle
        ): TorrentSessionStatus = TorrentSessionStatus(
            hash = task.hash,
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