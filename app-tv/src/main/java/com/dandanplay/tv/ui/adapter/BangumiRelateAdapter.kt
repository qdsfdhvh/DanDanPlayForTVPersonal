package com.dandanplay.tv.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.dandanplay.tv.databinding.ItemBangumiRelatedBinding
import com.dandanplay.tv.util.diff.BangumiIntroEntityDiffCallback
import com.dandanplay.tv.util.getBangumiStatus
import com.dandanplay.tv.util.scaleAnimator
import com.seiko.common.extensions.lazyAndroid
import com.seiko.core.data.db.model.BangumiIntroEntity
import kotlin.properties.Delegates

class BangumiRelateAdapter : BaseAdapter<BangumiRelateAdapter.BangumiRelateViewHolder>()
//    , UpdatableAdapter
    , View.OnFocusChangeListener {

//    private val diffCallback by lazyAndroid { BangumiIntroEntityDiffCallback() }
//
//    var items: List<BangumiIntroEntity> by Delegates.observable(emptyList()) { _, old, new ->
//        update(old, new, diffCallback)
//    }

    var items: List<BangumiIntroEntity> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun get(position: Int): BangumiIntroEntity? {
        if (position < 0 || position >= items.size) return null
        return items[position]
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BangumiRelateViewHolder {
        val binding = ItemBangumiRelatedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.onFocusChangeListener = this
        return BangumiRelateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BangumiRelateViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onPayload(holder: BangumiRelateViewHolder, bundle: Bundle) {
        holder.payload(bundle)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v == null) return

        LogUtils.d("view = $v, hasFocus=$hasFocus")
        v.scaleAnimator(hasFocus, 1.2f, 150)
    }

    inner class BangumiRelateViewHolder(private val binding: ItemBangumiRelatedBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != -1) {
                    listener?.onClick(this, items[position], position)
                }
            }
        }

        fun bind(item: BangumiIntroEntity) {
            binding.img.setImageURI(item.imageUrl)
            binding.title.text = item.animeTitle
            binding.chapter.text = item.getBangumiStatus()
        }

        fun payload(bundle: Bundle) {
            if (bundle.containsKey(BangumiIntroEntityDiffCallback.ARGS_ANIME_IMAGE_URL)) {
                binding.img.setImageURI(bundle.getString(BangumiIntroEntityDiffCallback.ARGS_ANIME_IMAGE_URL))
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