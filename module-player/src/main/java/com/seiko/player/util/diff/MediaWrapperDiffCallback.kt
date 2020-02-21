package com.seiko.player.util.diff

import androidx.recyclerview.widget.DiffUtil
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem

class MediaWrapperDiffCallback : DiffUtil.ItemCallback<MediaWrapper>() {

    companion object {
        const val UPDATE_PAYLOAD = 1
    }

    private var preventNextAnim: Boolean = false

    override fun areItemsTheSame(oldItem: MediaWrapper, newItem: MediaWrapper): Boolean {
        return if (preventNextAnim) {
            true
        } else {
            oldItem === newItem || oldItem.itemType == newItem.itemType && oldItem.equals(newItem)
        }
    }

    override fun areContentsTheSame(oldItem: MediaWrapper, newItem: MediaWrapper): Boolean {
        return false
    }

    override fun getChangePayload(oldItem: MediaWrapper, newItem: MediaWrapper): Any? {
        preventNextAnim = false
        return UPDATE_PAYLOAD
    }
}