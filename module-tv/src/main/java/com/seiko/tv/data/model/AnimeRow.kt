package com.seiko.tv.data.model

import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DiffCallback
import androidx.leanback.widget.Presenter

class AnimeRow<T>(private val id: Int = 0) {

    private var adapter: ArrayObjectAdapter? = null
//    private var details: DetailsOverviewRow? = null
    private var title: String? = null
    private var diffCallback: DiffCallback<T>? = null

    fun getId(): Long {
        return id.toLong()
    }

    fun getAdapter(): ArrayObjectAdapter? {
        return adapter
    }

    fun getTitle(): String? {
        return title
    }

    fun setAdapter(presenter: Presenter): AnimeRow<T> {
        this.adapter = ArrayObjectAdapter(presenter)
        return this
    }

    fun setAdapter(adapter: ArrayObjectAdapter): AnimeRow<T> {
        this.adapter = adapter
        return this
    }

    fun setTitle(title: String): AnimeRow<T> {
        this.title = title
        return this
    }

    fun setDiffCallback(callback: DiffCallback<T>): AnimeRow<T> {
        this.diffCallback = callback
        return this
    }

    fun setList(list: List<Any>?) {
        if (list == null) return
        adapter?.run {
            setItems(list, diffCallback)
        }

    }

//    fun setDetails(details: DetailsOverviewRow): AnimeRow {
//        this.details = details
//        return this
//    }
//
//    fun setDetailsItem(item: Any) {
//        details?.run {
//            setItem(item)
//        }
//    }

}