package com.seiko.player.util.diff

import androidx.recyclerview.widget.DiffUtil
import org.videolan.medialibrary.media.MediaLibraryItem

class MediaLibraryItemDiffCallback : DiffUtil.ItemCallback<MediaLibraryItem>() {

    companion object {
        const val UPDATE_PAYLOAD = 1
    }

    private var preventNextAnim: Boolean = false

    override fun areItemsTheSame(oldItem: MediaLibraryItem, newItem: MediaLibraryItem): Boolean {
        return if (preventNextAnim) {
            true
        } else {
            oldItem === newItem || oldItem.itemType == newItem.itemType && oldItem.equals(newItem)
        }
    }

    override fun areContentsTheSame(oldItem: MediaLibraryItem, newItem: MediaLibraryItem): Boolean {
        return false
    }

    override fun getChangePayload(oldItem: MediaLibraryItem, newItem: MediaLibraryItem): Any? {
        preventNextAnim = false
        return UPDATE_PAYLOAD
    }
}