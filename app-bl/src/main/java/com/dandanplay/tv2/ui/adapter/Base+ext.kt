package com.dandanplay.tv2.ui.adapter

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

abstract class BaseAdapter : RecyclerView.Adapter<BaseAdapter.BaseViewHolder>() {

//    abstract fun getLayoutManager(): RecyclerView.LayoutManager
//
//    open fun attach(recView: RecyclerView) {
//        recView.setHasFixedSize(true)
//        recView.layoutManager = getLayoutManager()
//        recView.adapter = this
//    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val bundle = payloads[0]
            if (bundle is Bundle) {
                holder.payload(position, bundle)
            }
        }
    }

    abstract class BaseViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        open fun attach(recView: RecyclerView) {

        }

        open fun bind(position: Int) {

        }

        open fun payload(position: Int, bundle: Bundle) {

        }
    }

}

/*****************************************************
 *                  Tv Presenter                     *
 *****************************************************/

//abstract class BasePresenter : Presenter() {
//
//    override fun onBindViewHolder(holder: ViewHolder?, item: Any?) {
//        if (holder is BaseViewHolder) {
//            holder.bind(item)
//        }
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder?, item: Any?, payloads: MutableList<Any>?) {
//        if (payloads == null || payloads.isEmpty()) {
//            onBindViewHolder(holder, item)
//        } else {
//            val bundle = payloads[0]
//            if (bundle is Bundle) {
//                if (holder is BaseViewHolder) {
//                    holder.payload(item, bundle)
//                }
//            }
//        }
//    }
//
//    override fun onUnbindViewHolder(holder: ViewHolder?) {
//        if (holder is BaseViewHolder) {
//            holder.unbind()
//        }
//    }
//
//    abstract class BaseViewHolder(
//        override val containerView: View
//    ) : Presenter.ViewHolder(containerView), LayoutContainer {
//
//        open fun bind(item: Any?) {
//
//        }
//
//        open fun payload(item: Any?, bundle: Bundle) {
//
//        }
//
//        open fun unbind() {
//
//        }
//    }
//
//}