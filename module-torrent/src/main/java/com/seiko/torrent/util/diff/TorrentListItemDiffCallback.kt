package com.seiko.torrent.util.diff

import android.os.Bundle
import androidx.leanback.widget.DiffCallback
import com.seiko.torrent.data.model.torrent.TorrentListItem

class TorrentListItemDiffCallback : DiffCallback<TorrentListItem>() {
    companion object {
        const val ARGS_TORRENT_TITLE = "ARGS_TORRENT_TITLE"
        const val ARGS_TORRENT_STATE = "ARGS_TORRENT_STATE"
//        const val ARGS_TORRENT_DOWNLOAD_PATH = "ARGS_TORRENT_DOWNLOAD_PATH"
//        const val ARGS_TORRENT_DATE_ADDED = "ARGS_TORRENT_DATE_ADDED"
        const val ARGS_TORRENT_ERROR = "ARGS_TORRENT_ERROR"
        const val ARGS_TORRENT_PROGRESS = "ARGS_TORRENT_PROGRESS"
        const val ARGS_TORRENT_RECEIVED_BYTES = "ARGS_TORRENT_RECEIVED_BYTES"
//        const val ARGS_TORRENT_UPLOADED_BYTES = "ARGS_TORRENT_UPLOADED_BYTES"
        const val ARGS_TORRENT_TOTAL_BYTES = "ARGS_TORRENT_TOTAL_BYTES"
        const val ARGS_TORRENT_DOWNLOAD_SPEED = "ARGS_TORRENT_DOWNLOAD_SPEED"
        const val ARGS_TORRENT_UPLOAD_SPEED = "ARGS_TORRENT_UPLOAD_SPEED"
        const val ARGS_TORRENT_ETA = "ARGS_TORRENT_ETA"
        const val ARGS_TORRENT_TOTAL_PEERS = "ARGS_TORRENT_TOTAL_PEERS"
        const val ARGS_TORRENT_PEERS = "ARGS_TORRENT_PEERS"
    }

    override fun areItemsTheSame(oldItem: TorrentListItem, newItem: TorrentListItem): Boolean {
        return oldItem.hash == newItem.hash
    }

    override fun areContentsTheSame(oldItem: TorrentListItem, newItem: TorrentListItem): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: TorrentListItem, newItem: TorrentListItem): Any? {
        val bundle = Bundle()
        if (oldItem.title != newItem.title) {
            bundle.putString(ARGS_TORRENT_TITLE, newItem.title)
        }
        if (oldItem.stateCode != newItem.stateCode) {
            bundle.putInt(ARGS_TORRENT_STATE, newItem.stateCode)
        }
//        if (oldItem.downloadPath != newItem.downloadPath) {
//            bundle.putString(ARGS_TORRENT_DOWNLOAD_PATH, newItem.downloadPath)
//        }
//        if (oldItem.dateAdded != newItem.dateAdded) {
//            bundle.putLong(ARGS_TORRENT_DATE_ADDED, newItem.dateAdded)
//        }
//        if (oldItem.uploadedBytes != newItem.uploadedBytes) {
//            bundle.putLong(ARGS_TORRENT_UPLOADED_BYTES, newItem.uploadedBytes)
//        }
        if (oldItem.progress != newItem.progress
            || oldItem.receivedBytes != newItem.receivedBytes
            || oldItem.totalBytes != newItem.totalBytes
            || oldItem.ETA != newItem.ETA) {
            bundle.putFloat(ARGS_TORRENT_PROGRESS, newItem.progress)
            bundle.putLong(ARGS_TORRENT_RECEIVED_BYTES, newItem.receivedBytes)
            bundle.putLong(ARGS_TORRENT_TOTAL_BYTES, newItem.totalBytes)
            bundle.putLong(ARGS_TORRENT_ETA, newItem.ETA)
        }
        if (oldItem.downloadSpeed != newItem.downloadSpeed
            || oldItem.uploadSpeed != newItem.uploadSpeed) {
            bundle.putLong(ARGS_TORRENT_DOWNLOAD_SPEED, newItem.downloadSpeed)
            bundle.putLong(ARGS_TORRENT_UPLOAD_SPEED, newItem.uploadSpeed)
        }
        if (oldItem.peers != newItem.peers
            || oldItem.totalPeers != newItem.totalPeers) {
            bundle.putInt(ARGS_TORRENT_PEERS, newItem.peers)
            bundle.putInt(ARGS_TORRENT_TOTAL_PEERS, newItem.totalPeers)
        }
        if (oldItem.error != newItem.error) {
            bundle.putString(ARGS_TORRENT_ERROR, newItem.error)
        }
        return if (bundle.isEmpty) null else bundle
    }

}