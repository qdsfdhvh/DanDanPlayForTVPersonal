package com.dandanplay.tv.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.dandanplay.tv.databinding.ItemBangumiSeasonBinding
import com.dandanplay.tv.util.diff.BangumiSeasonDiffCallback
import com.seiko.core.model.api.BangumiSeason

class BangumiSeasonAdapter : BaseAdapter<BangumiSeasonAdapter.ViewHolder>() {

    private var selectPosition = 0

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
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun onPayload(holder: ViewHolder, bundle: Bundle) {
        holder.payload(bundle)
    }

    fun setSelectPosition(position: Int) {
        selectPosition = position
    }

    inner class ViewHolder(private val binding: ItemBangumiSeasonBinding)
        : RecyclerView.ViewHolder(binding.root)
        , View.OnFocusChangeListener {

        init {
            itemView.onFocusChangeListener = this
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != -1) {
                    listener?.onClick(this, items[position], position)
                }
            }
        }

        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if (v == null) return
            val position = adapterPosition
            v.isSelected = selectPosition == position
            LogUtils.d("$position isSelected=${v.isSelected} hasFocus=$hasFocus")
        }

        fun bind(item: BangumiSeason, position: Int) {
            binding.title.text = item.seasonName
            itemView.isSelected = selectPosition == position
        }

        fun payload(bundle: Bundle) {
            if (bundle.containsKey(BangumiSeasonDiffCallback.ARGS_ANIME_TITLE)) {
                binding.title.text = bundle.getString(BangumiSeasonDiffCallback.ARGS_ANIME_TITLE)
            }
        }
    }
}