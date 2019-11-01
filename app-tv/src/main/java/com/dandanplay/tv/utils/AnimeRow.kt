package com.dandanplay.tv.utils

import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.Presenter

class AnimeRow(private val id: Int = 0) {

    private var adapter: ArrayObjectAdapter? = null
//    private var details: DetailsOverviewRow? = null
    private var title: String? = null

    fun getId(): Long {
        return id.toLong()
    }

    fun getAdapter(): ArrayObjectAdapter? {
        return adapter
    }

    fun getTitle(): String? {
        return title
    }

    fun setAdapter(presenter: Presenter): AnimeRow {
        this.adapter = ArrayObjectAdapter(presenter)
        return this
    }

    fun setAdapter(adapter: ArrayObjectAdapter): AnimeRow {
        this.adapter = adapter
        return this
    }

    fun setTitle(title: String): AnimeRow {
        this.title = title
        return this
    }

    fun setList(list: List<Any>?) {
        if (list == null) return
        adapter?.run {
            clear()
            addAll(0, list)
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