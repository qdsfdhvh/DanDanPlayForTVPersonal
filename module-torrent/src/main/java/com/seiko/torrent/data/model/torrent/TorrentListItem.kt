/*
 * Copyright (C) 2018 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of LibreTorrent.
 *
 * LibreTorrent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibreTorrent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibreTorrent.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.seiko.torrent.data.model.torrent

import com.seiko.download.torrent.annotation.TorrentStateCode

/*
 * An item of TorrentListAdapter
 */
data class TorrentListItem(
    var hash: String = "",
    var title: String = "",
    var stateCode: Int = TorrentStateCode.STOPPED,
    var downloadPath: String = "",
    var dateAdded: Long = 0L,
    var error: String = "",
    var progress: Float = 0F,
    var receivedBytes: Long = 0L,
    var uploadedBytes: Long = 0L,
    var totalBytes: Long = 0L,
    var downloadSpeed: Long = 0L,
    var uploadSpeed: Long = 0L,
    var ETA: Long = 0L,
    var totalPeers: Int = 0,
    var peers: Int = 0
) : Comparable<TorrentListItem> {

    override fun compareTo(other: TorrentListItem): Int {
        return hash.compareTo(other.hash)
    }

    override fun hashCode(): Int {
        return hash.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is TorrentListItem) return false
        return hash == other.hash
                && stateCode == other.stateCode
                && downloadPath == other.downloadPath
                && dateAdded == other.dateAdded
                && error == other.error

                && progress == other.progress
                && receivedBytes == other.receivedBytes
                && uploadedBytes == other.uploadedBytes
                && totalBytes == other.totalBytes
                && downloadSpeed == other.downloadSpeed
                && uploadSpeed == other.uploadSpeed
                && ETA == other.ETA
                && totalPeers == other.totalPeers
                && peers == other.peers
    }
}