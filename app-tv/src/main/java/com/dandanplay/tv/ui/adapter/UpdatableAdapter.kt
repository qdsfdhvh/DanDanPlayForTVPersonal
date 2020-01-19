package com.dandanplay.tv.ui.adapter

import androidx.leanback.widget.DiffCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


/**
 * Interface for adapters that can be updated through DiffUtil.
 * Updatable adapters hold a list of items of type T.
 */
interface UpdatableAdapter {

    /**
     * Update an adapter through DiffUtil and then dispatch the updates.
     *
     * @param oldItems the old list of items held by the adapter
     * @param newItems the new list of items held by the adapter
     */
    fun <T> RecyclerView.Adapter<*>.update(
        oldItems: List<T>,
        newItems: List<T>,
        diffCallback: DiffCallback<T>
    ) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return diffCallback.areItemsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return diffCallback.areItemsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])
            }

            override fun getOldListSize() = oldItems.size

            override fun getNewListSize() = newItems.size

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                return diffCallback.getChangePayload(oldItems[oldItemPosition], newItems[newItemPosition])
            }
        })
        diff.dispatchUpdatesTo(this)
    }
}