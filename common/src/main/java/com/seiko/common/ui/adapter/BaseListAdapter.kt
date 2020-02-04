package com.seiko.common.ui.adapter

import android.os.Bundle
import androidx.leanback.widget.DiffCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseListAdapter<T, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffCallback) {

    /**
     * 目前项目中用的leanback中的DiffCallback较多
     */
    constructor(diffCallback: DiffCallback<T>): this(diffCallback.toItemCallback())

    protected var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNullOrEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val bundle = payloads[0]
            if (bundle is Bundle) {
                onPayload(holder, bundle)
            }
        }
    }

    open fun onPayload(holder: VH, bundle: Bundle) {

    }
}

/**
 * 将[DiffCallback]转为[DiffUtil.ItemCallback]
 */
private fun <T> DiffCallback<T>.toItemCallback(): DiffUtil.ItemCallback<T> {
    return object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = this@toItemCallback.areItemsTheSame(oldItem, newItem)
        override fun areContentsTheSame(oldItem: T, newItem: T) = this@toItemCallback.areContentsTheSame(oldItem, newItem)
        override fun getChangePayload(oldItem: T, newItem: T): Any? = this@toItemCallback.getChangePayload(oldItem, newItem)
    }
}