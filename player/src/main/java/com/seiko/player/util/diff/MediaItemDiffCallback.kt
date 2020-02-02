package com.seiko.player.util.diff

import androidx.leanback.widget.DiffCallback
import org.videolan.medialibrary.media.MediaLibraryItem

class MediaItemDiffCallback<T : MediaLibraryItem> : DiffCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}