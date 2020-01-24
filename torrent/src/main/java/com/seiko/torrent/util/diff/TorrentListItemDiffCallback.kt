package com.seiko.torrent.util.diff

import android.os.Bundle
import androidx.leanback.widget.DiffCallback
import com.seiko.torrent.model.TorrentListItem

class TorrentListItemDiffCallback : DiffCallback<TorrentListItem>() {
    companion object {
        const val ARGS_TORRENT_TITLE = "ARGS_TORRENT_TITLE"
        const val ARGS_TORRENT_STATE = "ARGS_TORRENT_STATE"
        const val ARGS_TORRENT_PROGRESS = "ARGS_TORRENT_PROGRESS"
        const val ARGS_TORRENT_DOWNLOAD_COUNTER = "ARGS_TORRENT_DOWNLOAD_COUNTER"
        const val ARGS_TORRENT_SPEED = "ARGS_TORRENT_SPEED"
        const val ARGS_TORRENT_PEERS = "ARGS_TORRENT_PEERS"
        const val ARGS_TORRENT_BUTTON_STATE = "ARGS_TORRENT_BUTTON_STATE"

    }

    override fun areItemsTheSame(oldItem: TorrentListItem, newItem: TorrentListItem): Boolean {
        return oldItem.hash == newItem.hash
    }

    override fun areContentsTheSame(oldItem: TorrentListItem, newItem: TorrentListItem): Boolean {
        return oldItem == newItem
    }

    // TODO 暂时用不到，后面再补全
    override fun getChangePayload(oldItem: TorrentListItem, newItem: TorrentListItem): Any? {
        val bundle = Bundle()
        if (oldItem.name != newItem.name) {
            bundle.putString(ARGS_TORRENT_TITLE, newItem.name)
        }


        return if (bundle.isEmpty) null else bundle
    }

}