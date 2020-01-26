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
import com.seiko.common.extensions.lazyAndroid
import com.seiko.common.ui.adapter.BaseAdapter
import com.seiko.common.ui.adapter.UpdatableAdapter
import com.dandanplay.tv.data.db.model.BangumiIntroEntity
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
        // 在外面包一层cardView，看着UI统一
        val cardView = BaseCardView(parent.context)
        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        cardView.onFocusChangeListener = this

        val binding = ItemBangumiRelatedBinding.inflate(
            LayoutInflater.from(cardView.context), cardView, true)
        return BangumiRelateViewHolder(binding, cardView)
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
        private val binding: ItemBangumiRelatedBinding,
        cardView: View
    ) : RecyclerView.ViewHolder(cardView) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != -1) {
                    listener?.onClick(this, items[position], position)
                }
            }
        }

        fun bind(item: BangumiIntroEntity) {
            binding.img.loadImage(item.imageUrl)
            binding.title.text = item.animeTitle
            binding.chapter.text = item.getBangumiStatus()
        }

        fun payload(bundle: Bundle) {
            if (bundle.containsKey(BangumiIntroEntityDiffCallback.ARGS_ANIME_IMAGE_URL)) {
                binding.img.loadImage(bundle.getString(BangumiIntroEntityDiffCallback.ARGS_ANIME_IMAGE_URL)!!)
            }
            if (bundle.containsKey(BangumiIntroEntityDiffCallback.ARGS_ANIME_TITLE)) {
                binding.title.text = bundle.getString(BangumiIntroEntityDiffCallback.ARGS_ANIME_TITLE)
            }
            if (bundle.containsKey(BangumiIntroEntityDiffCallback.ARGS_ANIME_STATUS)) {
                binding.chapter.text = bundle.getString(BangumiIntroEntityDiffCallback.ARGS_ANIME_STATUS)
            }
        }
    }
}