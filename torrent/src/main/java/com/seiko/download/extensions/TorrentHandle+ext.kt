package com.seiko.download.extensions

import android.net.Uri
import com.frostwire.jlibtorrent.AnnounceEntry
import com.frostwire.jlibtorrent.Priority
import com.frostwire.jlibtorrent.TorrentHandle
import java.io.File


/**
 * Get the bencode of the [TorrentHandle].
 */
internal fun TorrentHandle.getBencode(): ByteArray = torrentFile()
        ?.bencode()
        ?: ByteArray(0)

/**
 * Get the seeder count of the [TorrentHandle].
 */
internal fun TorrentHandle.getSeederCount(): Int = status().numSeeds()

/**
 * Get the upload rate of the [TorrentHandle] in bytes/second.
 */
internal fun TorrentHandle.getUploadRate(): Int = status().uploadRate()

/**
 * Get the download rate of the [TorrentHandle] in bytes/second.
 */
internal fun TorrentHandle.getDownloadRate(): Int = status().downloadRate()

/**
 * Get the total bytes wanted (to be downloaded) of the [TorrentHandle].
 */
internal fun TorrentHandle.getTotalWanted(): Long = status().totalWanted()

/**
 * Get the total bytes done (downloaded) of the [TorrentHandle].
 */
internal fun TorrentHandle.getTotalDone(): Long = status().totalDone()

/**
 * Get the progress of the [TorrentHandle].
 */
internal fun TorrentHandle.getProgress(): Float = status().progress()

/**
 * Get the Uri of the largest file including the [downloadLocation] of the [TorrentHandle].
 */
internal fun TorrentHandle.getLargestFileUri(downloadLocation: File): Uri = Uri.fromFile(File(
        downloadLocation
        , torrentFile().files().filePath(torrentFile().getLargestFileIndex())
))

/**
 * Get the largest file index of the [TorrentHandle].
 */
internal fun TorrentHandle.getLargestFileIndex(): Int = torrentFile().getLargestFileIndex()

/**
 * Prioritize the largest file of the [TorrentHandle].
 */
internal fun TorrentHandle.prioritizeLargestFile(
        priority: Priority
) = filePriority(getLargestFileIndex(), priority)


/**
 * Ignore all of the files of the [TorrentHandle].
 */
internal fun TorrentHandle.ignoreAllFiles() = prioritizeFiles(
        Array(torrentFile().numFiles()) { Priority.IGNORE }
)

internal fun TorrentHandle.getStartPieceIndex(fileIndex: Int): Int =
        torrentFile()
                .mapFile(fileIndex, 0, 1)
                .piece()

internal fun TorrentHandle.getFileSize(fileIndex: Int): Long =
        torrentFile()
                .files()
                .fileSize(fileIndex)

internal fun TorrentHandle.getEndPieceIndex(fileIndex: Int): Int =
        torrentFile()
                .mapFile(fileIndex, getFileSize(fileIndex) - 1, 1)
                .piece()

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
internal inline fun TorrentHandle.hash() = infoHash().toHex()

internal fun TorrentHandle.addTrackers(trackers: List<String>) {
    for (tracker in trackers) {
        addTracker(AnnounceEntry(tracker))
    }
}