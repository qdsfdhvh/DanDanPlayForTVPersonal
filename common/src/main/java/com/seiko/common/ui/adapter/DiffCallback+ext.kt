package com.seiko.common.ui.adapter

import androidx.leanback.widget.DiffCallback
import androidx.recyclerview.widget.DiffUtil

/**
 * 将[DiffCallback]转为[DiffUtil.ItemCallback]
 */
internal fun <T> DiffCallback<T>.toItemCallback(): DiffUtil.ItemCallback<T> {
    return object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = this@toItemCallback.areItemsTheSame(oldItem, newItem)
        override fun areContentsTheSame(oldItem: T, newItem: T) = this@toItemCallback.areContentsTheSame(oldItem, newItem)
        override fun getChangePayload(oldItem: T, newItem: T): Any? = this@toItemCallback.getChangePayload(oldItem, newItem)
    }
}