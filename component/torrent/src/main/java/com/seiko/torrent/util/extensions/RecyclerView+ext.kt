package com.seiko.torrent.util.extensions

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

/**
 * A RecyclerView by default creates another copy of the ViewHolder in order to
 * fade the views into each other. This causes the problem because the old ViewHolder gets
 * the payload but then the new one doesn't. So needs to explicitly tell it to reuse the old one.
 */
fun RecyclerView.fixItemAnim() {
    itemAnimator = object : DefaultItemAnimator() {
        override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
            return true
        }
    }
}