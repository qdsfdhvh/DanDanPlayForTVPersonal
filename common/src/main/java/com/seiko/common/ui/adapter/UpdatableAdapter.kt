package com.seiko.common.ui.adapter

import androidx.leanback.widget.DiffCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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
        diffCallback: DiffCallback<T>,
        detectMoves: Boolean = true
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
        }, detectMoves)
        diff.dispatchUpdatesTo(this)
    }

    suspend fun <T> RecyclerView.Adapter<*>.updateCoroutine(
        oldItems: List<T>,
        newItems: List<T>,
        diffCallback: DiffCallback<T>,
        detectMoves: Boolean = true
    ) {
        val diff = withContext(Dispatchers.Default) {
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {

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
            }, false)
        }
        diff.dispatchUpdatesTo(this)
    }
}