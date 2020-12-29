package com.seiko.common.ui.adapter

import androidx.leanback.widget.DiffCallback
import androidx.leanback.widget.ObjectAdapter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback

class AsyncPagedObjectAdapter<T : Any> : ObjectAdapter {

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

    private val differ: AsyncPagingDataDiffer<T>

    constructor(presenter: Presenter, diffCallback: DiffCallback<T>) : this(
        presenter,
        diffCallback.toItemCallback()
    )

    constructor(presenter: Presenter, diffCallback: DiffUtil.ItemCallback<T>) : super(presenter) {
        differ = AsyncPagingDataDiffer(
            diffCallback = diffCallback,
            updateCallback = updateCallback
        )
    }

    constructor(presenterSelector: PresenterSelector, diffCallback: DiffCallback<T>) : this(
        presenterSelector,
        diffCallback.toItemCallback()
    )

    constructor(
        presenterSelector: PresenterSelector,
        diffCallback: DiffUtil.ItemCallback<T>
    ) : super(presenterSelector) {
        differ = AsyncPagingDataDiffer(
            diffCallback = diffCallback,
            updateCallback = updateCallback
        )
    }

    suspend fun submitData(pagingData: PagingData<T>) {
        differ.submitData(pagingData)
    }

    override fun size(): Int {
        return differ.itemCount
    }

    override fun get(position: Int): Any {
        return differ.getItem(position)!!
    }

}

