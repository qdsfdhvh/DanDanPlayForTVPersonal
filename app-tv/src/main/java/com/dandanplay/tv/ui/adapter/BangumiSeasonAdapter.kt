package com.dandanplay.tv.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.dandanplay.tv.databinding.ItemBangumiSeasonBinding
import com.dandanplay.tv.util.diff.BangumiSeasonDiffCallback
import com.dandanplay.tv.util.scaleAnimator
import com.seiko.common.extensions.lazyAndroid
import com.seiko.core.model.api.BangumiSeason
import kotlin.properties.Delegates

class BangumiSeasonAdapter : BaseAdapter<BangumiSeasonAdapter.ViewHolder>()
    , UpdatableAdapter
    , View.OnFocusChangeListener {

//    private val diffCallback by lazyAndroid { BangumiSeasonDiffCallback() }
//
//    var items: List<BangumiSeason> by Delegates.observable(emptyList()) { _, old, new ->
//        update(old, new, diffCallback)
//    }

    var items: List<BangumiSeason> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun get(position: Int): BangumiSeason? {
        if (position < 0 || position >= items.size) return null
        return items[position]
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBangumiSeasonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.onFocusChangeListener = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onPayload(holder: ViewHolder, bundle: Bundle) {
        holder.payload(bundle)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v == null) return

        LogUtils.d("view = $v, hasFocus=$hasFocus")
//        v.scaleAnimator(hasFocus, 1f, 0)
    }

    inner class ViewHolder(private val binding: ItemBangumiSeasonBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != -1) {
                    listener?.onClick(this, items[position], position)
                }
            }
        }

        fun bind(item: BangumiSeason) {
            binding.title.text = item.seasonName
        }

        fun payload(bundle: Bundle) {
            if (bundle.containsKey(BangumiSeasonDiffCallback.ARGS_ANIME_TITLE)) {
                binding.title.text = bundle.getString(BangumiSeasonDiffCallback.ARGS_ANIME_TITLE)
            }
        }
    }
}