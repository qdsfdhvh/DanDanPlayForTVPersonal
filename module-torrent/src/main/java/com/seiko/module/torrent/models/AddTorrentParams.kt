package com.seiko.module.torrent.models

import android.os.Parcelable
import com.seiko.torrent.models.TorrentTask
import kotlinx.android.parcel.Parcelize
import org.libtorrent4j.Priority

@Parcelize
data class AddTorrentParams(
    val source: String,
    val isFromMagnet: Boolean,
    val sha1hash: String,
    val name: String,
    val filePriorities: List<Priority>,
    val pathToDownload: String,
    val sequentialDownload: Boolean,
    val addPaused: Boolean
): Parcelable {

    override fun hashCode(): Int {
        return sha1hash.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (this === other) {
            return true
        }
        if (other !is AddTorrentParams) {
            return false
        }
        return source == other.source
                && isFromMagnet == other.isFromMagnet
                && sha1hash == other.sha1hash
                && name == other.name
                && filePriorities == other.filePriorities
                && pathToDownload == other.pathToDownload
                && sequentialDownload == other.sequentialDownload
                && addPaused == other.addPaused
    }

    override fun toString(): String {
        return "AddTorrentParams{" +
                "source='" + source + '\''.toString() +
                ", isFromMagnet=" + isFromMagnet +
                ", sha1hash='" + sha1hash + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", filePriorities=" + filePriorities +
                ", pathToDownload='" + pathToDownload + '\''.toString() +
                ", sequentialDownload=" + sequentialDownload +
                ", addPaused=" + addPaused +
                '}'.toString()
    }
}

internal fun AddTorrentParams.toTask(): TorrentTask {
    return TorrentTask(
        hash = sha1hash,
        name = name,
        priorityList = filePriorities,
        downloadPath = pathToDownload,
        addedDate = System.currentTimeMillis(),

        source = source,
        sequentialDownload = sequentialDownload,
        paused = addPaused)
}