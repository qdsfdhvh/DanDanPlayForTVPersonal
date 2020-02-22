package com.seiko.common.ui.adapter

import androidx.leanback.widget.DiffCallback
import androidx.leanback.widget.ObjectAdapter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback

class AsyncPagedObjectAdapter<T : Any>: ObjectAdapter {

    private val updateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            notifyItemRangeChanged(position, count, payload)
        }
    }

    private val differ: AsyncPagedListDiffer<T>

    constructor(presenter: Presenter, diffCallback: DiffCallback<T>) : this(presenter, diffCallback.toItemCallback())
    constructor(presenter: Presenter, diffCallback: DiffUtil.ItemCallback<T>) : this(presenter, AsyncDifferConfig.Builder(diffCallback).build())
    constructor(presenter: Presenter, config: AsyncDifferConfig<T>) : super(presenter) {
        differ = AsyncPagedListDiffer(updateCallback, config)
    }

    constructor(presenterSelector: PresenterSelector, diffCallback: DiffCallback<T>) : this(presenterSelector, diffCallback.toItemCallback())
    constructor(presenterSelector: PresenterSelector, diffCallback: DiffUtil.ItemCallback<T>) : this(presenterSelector, AsyncDifferConfig.Builder(diffCallback).build())
    constructor(presenterSelector: PresenterSelector, config: AsyncDifferConfig<T>) :super(presenterSelector) {
        differ = AsyncPagedListDiffer(updateCallback, config)
    }

    fun submitList(list: PagedList<T>) {
        differ.submitList(list)
    }

    fun submitList(list: PagedList<T>, commitCallback: () -> Unit?) {
        differ.submitList(list) { commitCallback.invoke() }
    }

    override fun size(): Int {
        return differ.itemCount
    }

    override fun get(position: Int): Any {
        return differ.getItem(position)!!
    }

}

