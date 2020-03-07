package com.seiko.download.torrent.extensions

import com.seiko.download.torrent.annotation.TorrentStateCode
import org.libtorrent4j.TorrentFlags
import org.libtorrent4j.TorrentHandle
import org.libtorrent4j.TorrentStatus
import kotlin.math.min

internal fun TorrentStatus.isPaused(): Boolean {
    return flags().and_(TorrentFlags.PAUSED).nonZero()
}

/**
 * 当前状态
 */
@TorrentStateCode
internal fun TorrentHandle.getStateCode(): Int {
    val status = status() ?: return TorrentStateCode.ERROR
    val isPaused = status().isPaused()

    if (isPaused && status.isFinished) {
        return TorrentStateCode.FINISHED
    } else if (isPaused && !status.isFinished) {
        return TorrentStateCode.PAUSED
    } else if (!isPaused && status.isFinished) {
        return TorrentStateCode.SEEDING
    }

    return when(status.state()) {
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

/**
 * 已连接的种子数
 */
internal fun TorrentHandle.getConnectSeeds(): Int = status()?.numSeeds() ?: 0

/**
 * 总的种子数
 */
internal fun TorrentHandle.getTotalSeeds(): Int = status()?.listSeeds() ?: 0

/**
 * 已连接的用户数
 */
internal fun TorrentHandle.getConnectedPeers(): Int = status()?.numPeers() ?: 0

/**
 * 总用户数
 */
internal fun TorrentHandle.getTotalPeers(): Int = status()?.listPeers() ?: 0

/**
 * 已下载的数据大小
 */
internal fun TorrentHandle.getTotalDone(): Long = status()?.totalDone() ?: 0

/**
 * 需要的数据总大小
 */
internal fun TorrentHandle.getTotalWanted(): Long = status()?.totalWanted() ?: 0

/**
 * 这次已下载的数据大小
 */
internal fun TorrentHandle.getReceivedBytes(): Long = status()?.totalPayloadDownload() ?: 0

/**
 * 总的已下载的数据大小
 */
internal fun TorrentHandle.getTotalReceivedBytes(): Long = status()?.allTimeDownload() ?: 0

/**
 * 这次已上传的数据大小
 */
internal fun TorrentHandle.getSentBytes(): Long = status()?.totalPayloadUpload() ?: 0

/**
 * 总的已上传的数据大小
 */
internal fun TorrentHandle.getTotalSendBytes(): Long = status()?.allTimeUpload() ?: 0

/**
 * 总的文件大小
 */
internal fun TorrentHandle.getTotalSize(): Long = torrentFile()?.totalSize() ?: 0

/**
 * 进度
 */
internal fun TorrentHandle.getProgress(): Float {
    val status = status() ?: return 0f

    val fp = status.progress()
    val state = status.state()
    if (fp.compareTo(1f) == 0 && state != TorrentStatus.State.CHECKING_FILES) {
        return 100f
    }

    var p = status.progress() * 100
    if (p > 0 && state != TorrentStatus.State.CHECKING_FILES) {
        return min(p, 100f)
    }

    val received = getTotalReceivedBytes()
    val size = getTotalSize()
    if (received.compareTo(size) == 0) {
        return 100f
    } else if (size > 0) {
        p = (received * 100f) / size
        return min(p, 100f)
    }
    return 0f
}

/**
 * 当前上传速度
 */
internal fun TorrentHandle.getUploadSpeed(): Long = status()?.uploadPayloadRate()?.toLong() ?: 0

/**
 * 当前下载速度
 */
internal fun TorrentHandle.getDownloadSpeed(): Long = status()?.downloadPayloadRate()?.toLong() ?: 0

/**
 * 获得预计完成剩余时间
 */
internal fun TorrentHandle.getETA(): Long {
    val files = torrentFile() ?: return 0
    val status = status() ?: return 0
    val left = files.totalSize() - status.totalDone()
    if (left <= 0) return 0
    val rate = status.downloadPayloadRate()
    if (rate <= 0) return -1
    return left / rate
}

/**
 * Get the bencode of the [TorrentHandle].
 */
internal fun TorrentHandle.getBencode(): ByteArray = torrentFile()
    ?.bencode()
    ?: ByteArray(0)