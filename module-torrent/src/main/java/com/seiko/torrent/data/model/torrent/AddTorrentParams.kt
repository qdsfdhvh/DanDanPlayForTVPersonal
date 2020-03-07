package com.seiko.torrent.data.model.torrent

import android.os.Parcelable
import com.seiko.torrent.data.db.TorrentEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddTorrentParams(
    val entity: TorrentEntity,
    val fromMagnet: Boolean
): Parcelable {

    override fun hashCode(): Int {
        return entity.hash.hashCode()
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
        return fromMagnet == other.fromMagnet && entity == other.entity
    }

    override fun toString(): String {
        return "AddTorrentParams{" +
                "source='" + entity.source + '\''.toString() +
                ", isFromMagnet=" + fromMagnet +
                ", sha1hash='" + entity.hash + '\''.toString() +
                ", name='" + entity.name + '\''.toString() +
                ", filePriorities=" + entity.priorityList +
                ", pathToDownload='" + entity.downloadPath + '\''.toString() +
                ", sequentialDownload=" + entity.sequentialDownload +
                ", addPaused=" + entity.paused +
                '}'.toString()
    }
}