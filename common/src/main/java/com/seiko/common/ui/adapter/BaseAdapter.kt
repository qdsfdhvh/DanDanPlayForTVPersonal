package com.seiko.common.ui.adapter

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {

    protected var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: T, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNullOrEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val bundle = payloads[0]
            if (bundle is Bundle) {
                onPayload(holder, bundle)
            }
        }
    }

    open fun onPayload(holder: T, bundle: Bundle) {

    }

}