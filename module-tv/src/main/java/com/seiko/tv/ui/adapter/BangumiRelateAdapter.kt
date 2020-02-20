package com.seiko.tv.ui.adapter

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.ui.adapter.BaseListAdapter
import com.seiko.common.ui.adapter.FocusAnimator
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.ui.card.MainAreaCardView
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback

class BangumiRelateAdapter : BaseListAdapter<HomeImageBean, BangumiRelateAdapter.BangumiRelateViewHolder>(HomeImageBeanDiffCallback()) {

    fun get(position: Int): HomeImageBean? {
        if (position < 0 || position >= itemCount) return null
        return getItem(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BangumiRelateViewHolder {
        val cardView = MainAreaCardView(parent.context)
        return BangumiRelateViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: BangumiRelateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onPayload(holder: BangumiRelateViewHolder, bundle: Bundle) {
        holder.payload(bundle)
    }

    inner class BangumiRelateViewHolder(
        private val cardView: MainAreaCardView
    ) : RecyclerView.ViewHolder(cardView)
        , View.OnClickListener {

        private val animator = FocusAnimator(cardView, 1.2f,  false, 150)

        init {
            cardView.setOnFocusChangeListener { _, hasFocus ->
                animator.animateFocus(hasFocus, false)
            }
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