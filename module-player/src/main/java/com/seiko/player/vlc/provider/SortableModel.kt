package com.seiko.player.vlc.provider

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.seiko.player.vlc.interfaces.RefreshModel
import com.seiko.player.vlc.interfaces.SortModule
import org.videolan.medialibrary.interfaces.Medialibrary

abstract class SortableModel(app: Application) : AndroidViewModel(app), RefreshModel, SortModule {

    var sort = Medialibrary.SORT_DEFAULT
    var desc = false

    protected open val sortKey: String = "SortableModel"

    fun getKey() = sortKey

    override fun sort(sort: Int) {
        if (canSortBy(sort)) {
            desc = when(this.sort) {
                Medialibrary.SORT_DEFAULT -> sort == Medialibrary.SORT_ALPHA
                sort -> !desc
                else -> false
            }
            this.sort = sort
            refresh()
        }
    }

    var filterQuery : String? = null

    abstract fun restore()
    abstract fun filter(query: String?)
}
