package com.seiko.player.vm

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.seiko.player.vlc.provider.MediaLibraryProvider
import com.seiko.player.vlc.provider.SortableModel
import org.videolan.medialibrary.media.MediaLibraryItem

abstract class MediaLibraryViewModel(app: Application) : SortableModel(app), ICallBackHandler by CallBackDelegate() {

    init {
        @Suppress("LeakingThis")
        viewModelScope.registerCallBacks { refresh() }
    }

    abstract val providers : Array<MediaLibraryProvider<out MediaLibraryItem>>

    override fun refresh() = providers.forEach { it.refresh() }

    fun isEmpty() = providers.all { it.isEmpty() }

    override fun restore() {
        if (filterQuery !== null) filter(null)
    }

    override fun filter(query: String?) {
        filterQuery = query
        refresh()
    }

    override fun sort(sort: Int) { providers.forEach { it.sort(sort) } }

    fun isFiltering() = filterQuery != null

    override fun onCleared() {
        releaseCallbacks()
        super.onCleared()
    }

    override fun canSortByName() = providers.any { it.canSortByName() }
    override fun canSortByFileNameName() = providers.any { it.canSortByFileNameName() }
    override fun canSortByDuration() = providers.any { it.canSortByDuration() }
    override fun canSortByInsertionDate() = providers.any { it.canSortByInsertionDate() }
    override fun canSortByLastModified() = providers.any { it.canSortByLastModified() }
    override fun canSortByReleaseDate() = providers.any { it.canSortByReleaseDate() }
    override fun canSortByFileSize() = providers.any { it.canSortByFileSize() }
    override fun canSortByArtist() = providers.any { it.canSortByArtist() }
    override fun canSortByAlbum () = providers.any { it.canSortByAlbum () }
    override fun canSortByPlayCount() = providers.any { it.canSortByPlayCount() }
}