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
package com.seiko.torrent.model

import com.seiko.core.data.db.model.TorrentEntity
import com.seiko.download.torrent.constants.TorrentStateCode
import com.seiko.download.torrent.model.TorrentSessionStatus
import com.seiko.download.torrent.model.TorrentTask

/*
 * An item of TorrentListAdapter
 */
data class TorrentListItem(
    var hash: String = "",
    var name: String = "",
    var downloadPath: String = "",
    var stateCode: Int = TorrentStateCode.STOPPED,
    var progress: Float = 0F,
    var receivedBytes: Long = 0L,
    var uploadedBytes: Long = 0L,
    var totalBytes: Long = 0L,
    var downloadSpeed: Long = 0L,
    var uploadSpeed: Long = 0L,
    var ETA: Long = 0L,
    var dateAdded: Long = 0L,
    var totalPeers: Int = 0,
    var peers: Int = 0,
    var error: String = ""
) : Comparable<TorrentListItem> {

    constructor(task: TorrentTask) : this(
        hash = task.hash,
        name = task.name,
        downloadPath = task.downloadPath,
        dateAdded = task.addedDate,
        error = task.error
    )

    constructor(entity: TorrentEntity) : this(
        hash = entity.hash,
        name = entity.name,
        downloadPath = entity.downloadPath,
        dateAdded = entity.addedDate,
        error = entity.error
    )

    constructor(state: TorrentSessionStatus) : this(
        hash = state.hash,
//        name = state.name,
        error = state.error,
        stateCode = state.state,
        progress = state.progress,
        receivedBytes = state.receivedBytes,
        uploadedBytes = state.uploadedBytes,
        totalBytes = state.totalBytes,
        downloadSpeed = state.downloadRate,
        uploadSpeed = state.uploadRate,
        ETA = state.eta,
        totalPeers = state.totalPeers,
        peers = state.connectPeers
    )

    fun update(state: TorrentSessionStatus) {
        stateCode = state.state
        progress = state.progress
        receivedBytes = state.receivedBytes
        uploadedBytes = state.uploadedBytes
        totalBytes = state.totalBytes
        downloadSpeed = state.downloadRate
        uploadSpeed = state.uploadRate
        ETA = state.eta
        totalPeers = state.totalPeers
        peers = state.connectPeers
        error = state.error
    }

    override fun compareTo(other: TorrentListItem): Int {
        return hash.compareTo(other.hash)
    }

    override fun hashCode(): Int {
        return hash.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is TorrentListItem && hash == other.hash
    }
}