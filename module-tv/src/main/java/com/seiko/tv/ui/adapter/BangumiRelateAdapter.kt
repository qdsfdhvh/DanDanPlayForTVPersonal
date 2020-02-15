package com.seiko.tv.ui.adapter

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.util.scaleAnimator
import com.seiko.common.ui.adapter.BaseListAdapter
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.ui.card.MainAreaCardView
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback

class BangumiRelateAdapter : BaseListAdapter<HomeImageBean, BangumiRelateAdapter.BangumiRelateViewHolder>(HomeImageBeanDiffCallback()),
    View.OnFocusChangeListener {

    fun get(position: Int): HomeImageBean? {
        if (position < 0 || position >= itemCount) return null
        return getItem(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BangumiRelateViewHolder {
        val cardView = MainAreaCardView(parent.context)
        cardView.onFocusChangeListener = this
        return BangumiRelateViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: BangumiRelateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onPayload(holder: BangumiRelateViewHolder, bundle: Bundle) {
        holder.payload(bundle)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v == null) return
        v.scaleAnimator(hasFocus, 1.2f, 150)
    }

    inner class BangumiRelateViewHolder(
        private val cardView: MainAreaCardView
    ) : RecyclerView.ViewHolder(cardView)
        , View.OnClickListener {

        init {
            cardView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != -1) {
                listener?.onClick(this, getItem(position), position)
            }
        }

        fun bind(item: HomeImageBean) {
            cardView.bind(item)
        }

        fun payload(bundle: Bundle) {
            cardView.bind(bundle)
        }
    }
}