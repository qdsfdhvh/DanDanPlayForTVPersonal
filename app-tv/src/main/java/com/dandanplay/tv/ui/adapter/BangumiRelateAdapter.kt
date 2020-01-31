package com.dandanplay.tv.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.BaseCardView
import androidx.recyclerview.widget.RecyclerView
import com.dandanplay.tv.databinding.ItemBangumiRelatedBinding
import com.dandanplay.tv.util.diff.BangumiIntroEntityDiffCallback
import com.dandanplay.tv.util.getBangumiStatus
import com.dandanplay.tv.util.loadImage
import com.seiko.common.util.scaleAnimator
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.ui.adapter.BaseAdapter
import com.seiko.common.ui.adapter.UpdatableAdapter
import com.dandanplay.tv.data.db.model.BangumiIntroEntity
import com.dandanplay.tv.ui.card.BangumiIntroEntityCardView
import kotlin.properties.Delegates

class BangumiRelateAdapter : BaseAdapter<BangumiRelateAdapter.BangumiRelateViewHolder>(),
    UpdatableAdapter,
    View.OnFocusChangeListener {

    private val diffCallback by lazyAndroid { BangumiIntroEntityDiffCallback() }

    var items: List<BangumiIntroEntity> by Delegates.observable(emptyList()) { _, old, new ->
        update(old, new, diffCallback)
    }

    fun get(position: Int): BangumiIntroEntity? {
        if (position < 0 || position >= items.size) return null
        return items[position]
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BangumiRelateViewHolder {
        val cardView = BangumiIntroEntityCardView(parent.context)
        cardView.onFocusChangeListener = this
        return BangumiRelateViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: BangumiRelateViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onPayload(holder: BangumiRelateViewHolder, bundle: Bundle) {
        holder.payload(bundle)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v == null) return
        v.scaleAnimator(hasFocus, 1.2f, 150)
    }

    inner class BangumiRelateViewHolder(
        private val cardView: BangumiIntroEntityCardView
    ) : RecyclerView.ViewHolder(cardView)
        , View.OnClickListener {

        init {
            cardView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != -1) {
                listener?.onClick(this, items[position], position)
            }
        }

        fun bind(item: BangumiIntroEntity) {
            cardView.bind(item)
        }

        fun payload(bundle: Bundle) {
            cardView.bind(bundle)
        }
    }
}