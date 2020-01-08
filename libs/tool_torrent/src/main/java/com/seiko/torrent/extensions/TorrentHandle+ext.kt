package com.seiko.torrent.extensions

import com.seiko.torrent.constants.TorrentStateCode
import com.seiko.torrent.model.AdvancedPeerInfo
import com.seiko.torrent.utils.log
import org.libtorrent4j.*
import org.libtorrent4j.swig.peer_info_vector
import kotlin.math.log
import kotlin.math.min

private fun TorrentStatus.isPaused(): Boolean {
    return flags().and_(TorrentFlags.PAUSED).nonZero()
}

//@TorrentStateCode
fun TorrentHandle.getStateCode(): Int {
    if (isPaused()) {
        return TorrentStateCode.PAUSED
    }

    if (!isValid) {
        return TorrentStateCode.ERROR
    }


    val isPaused = status().isPaused()

    when {
        isPaused && status().isFinished -> return TorrentStateCode.FINISHED
        isPaused && !status().isFinished -> return TorrentStateCode.PAUSED
        !isPaused && status().isFinished -> return TorrentStateCode.SEEDING
    }

    return when(status().state()) {
        TorrentStatus.State.CHECKING_FILES -> TorrentStateCode.CHECKING
        TorrentStatus.State.DOWNLOADING_METADATA -> TorrentStateCode.DOWNLOADING_METADATA
        TorrentStatus.State.DOWNLOADING -> TorrentStateCode.DOWNLOADING
        TorrentStatus.State.FINISHED -> TorrentStateCode.FINISHED
        TorrentStatus.State.SEEDING -> TorrentStateCode.SEEDING
        TorrentStatus.State.ALLOCATING -> TorrentStateCode.ALLOCATING
        TorrentStatus.State.CHECKING_RESUME_DATA -> TorrentStateCode.CHECKING
        else -> TorrentStateCode.UNKNOWN
    }
}


fun TorrentHandle.isSequentialDownload(): Boolean {
    return isValid && status().flags().and_(TorrentFlags.SEQUENTIAL_DOWNLOAD).nonZero()
}

fun TorrentHandle.isPaused(): Boolean {
    return isValid && status(true).isPaused()
}

fun TorrentHandle.isSeeding(): Boolean {
    return isValid && status().isSeeding
}

fun TorrentHandle.isFinished(): Boolean {
    return isValid && status().isFinished
}

fun TorrentHandle.getDownloadSpeed(): Long {
    if (!isValid || isFinished() || isSeeding() || isPaused()) {
        return 0
    }
    return status().downloadPayloadRate().toLong()
}

fun TorrentHandle.getUploadSpeed(): Long {
    if (!isValid || (isFinished() && !isSeeding()) || isPaused()) {
        return 0
    }
    return status().uploadPayloadRate().toLong()
}

fun TorrentHandle.getETA(): Long {
    if (!isValid) {
        return 0
    }

    if (getStateCode() != TorrentStateCode.DOWNLOADING) {
        return 0
    }

    val files = torrentFile() ?: return 0

    val status = status()
    val left = files.totalSize() - status.totalDone()
    if (left <= 0) return 0
    val rate = status.downloadPayloadRate()
    if (rate <= 0) return -1
    return left / rate
}

fun TorrentHandle.getActiveTime(): Long {
    return if (isValid) status().activeDuration() / 1000L else 0
}

fun TorrentHandle.getSeedingTime(): Long {
    return if (isValid) status().seedingDuration() / 1000L else 0
}

fun TorrentHandle.getReceivedBytes(): Long {
    return if (isValid) status().totalPayloadDownload() else 0
}

fun TorrentHandle.getTotalReceivedBytes(): Long {
    return if (isValid) status().allTimeDownload() else 0
}

fun TorrentHandle.getSentBytes(): Long {
    return if (isValid) status().totalPayloadUpload() else 0
}

fun TorrentHandle.getTotalSendBytes(): Long {
    return if (isValid) status().allTimeUpload() else 0
}

fun TorrentHandle.getConnectedPeers(): Int {
    return if (isValid) status().numPeers() else 0
}

fun TorrentHandle.getConnectedSeeds(): Int {
    return if (isValid) status().numSeeds() else 0
}

fun TorrentHandle.getTotalSeeds(): Int {
    return if (isValid) status().listSeeds() else 0
}

fun TorrentHandle.getTotalWanted(): Long {
    if (!isValid) {
        return 0
    }
    return status().totalWanted()
}

fun TorrentHandle.getTotalSize(): Long {
    if (!isValid) {
        return 0
    }

    return  torrentFile()?.totalSize() ?: 0
}

fun TorrentHandle.getTotalPeers(): Int {
    if (!isValid) {
        return 0
    }

    val status = status()
    val peers = status.numComplete() + status.numIncomplete()
    return if (peers > 0) peers else status.listPeers()
}

fun TorrentHandle.getProgress(): Int {
    if (!isValid || status() == null) {
        return 0
    }

    val fp = status().progress()
    val state = status().state()
    if (fp.compareTo(1f) == 0 && state != TorrentStatus.State.CHECKING_FILES) {
        return 100
    }

    var p = (fp * 100).toInt()
    if (p > 0 && state != TorrentStatus.State.CHECKING_FILES) {
        return min(p, 100)
    }

    val received = getTotalReceivedBytes()
    val size = getTotalSize()
    if (size == received) {
        return 100
    }
    if (size > 0) {
        p = (received * 100 / size).toInt()
        return min(p, 100)
    }
    return 0
}

fun TorrentHandle.getTrackersUrl(): Set<String> {
    if (!isValid) {
        return emptySet()
    }

    return trackers().map { it.url() }.toSet()
}

fun TorrentHandle.getTrackers(): List<AnnounceEntry> {
    if (!isValid) {
        return emptyList()
    }
    return trackers()
}

fun TorrentHandle.getAdvancedPeerInfoList(): List<AdvancedPeerInfo> {
    if (!isValid) {
        return emptyList()
    }

    val vector = peer_info_vector()
    swig().get_peer_info(vector)

    val size = vector.size().toInt()
    val list = ArrayList<AdvancedPeerInfo>(size)
    for (i in 0 until size) {
        list.add(AdvancedPeerInfo(vector.get(i)))
    }
    return list
}

fun TorrentHandle.safePrioritizeFiles(priorities: Array<Priority>?) {
    if (!isValid) {
        return
    }

    val info = torrentFile() ?: return
    if (priorities != null) {
        if (info.numFiles() != priorities.size) {
            return
        }
        prioritizeFiles(priorities)
    } else {
        val wholeTorrentPriorities = Priority.array(Priority.DEFAULT, info.numFiles())
        prioritizeFiles(wholeTorrentPriorities)
    }
}

fun TorrentHandle.setMaxConnections(connections: Int) {
    if (!isValid) {
        return
    }
    swig().set_max_connections(connections)
}

fun TorrentHandle.setMaxUploads(uploads: Int) {
    if (!isValid) {
        return
    }
    swig().set_max_uploads(uploads)
}