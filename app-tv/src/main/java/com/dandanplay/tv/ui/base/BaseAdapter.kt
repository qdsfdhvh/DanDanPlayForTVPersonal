package com.dandanplay.tv.ui.base

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {

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