package com.seiko.player.util.diff

import androidx.leanback.widget.DiffCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.videolan.medialibrary.media.MediaLibraryItem

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class MediaItemDiffCallback<T : MediaLibraryItem> : DiffCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}