package com.seiko.player.ui.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.ui.adapter.BaseListAdapter
import com.seiko.player.data.model.PlayOption
import com.seiko.player.databinding.PlayerItemOptionBinding
import com.seiko.player.util.diff.PlayOptionDiffCallback

class OptionsAdapter(context: Context) : BaseListAdapter<PlayOption, OptionsAdapter.OptionsViewHolder>(PlayOptionDiffCallback()) {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {
        val binding = PlayerItemOptionBinding.inflate(inflater, parent, false)
        return OptionsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onPayload(holder: OptionsViewHolder, bundle: Bundle) {
        holder.payload(bundle)
    }

    inner class OptionsViewHolder(
        private val binding: PlayerItemOptionBinding
    ): RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.isFocusable = true
            itemView.isFocusableInTouchMode = true
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position >= 0) {
                    listener?.onClick(this, getItem(position), position)
                }
            }
        }

        fun bind(position: Int) {
            val item = getItem(position)
            binding.playerOptionTitle.text = item.title
            binding.playerOptionIcon.setImageResource(item.icon)
        }

        fun payload(bundle: Bundle) {
            if (bundle.containsKey(PlayOptionDiffCallback.ARGS_OPTION_TITLE)) {
                binding.playerOptionTitle.text = bundle.getString(PlayOptionDiffCallback.ARGS_OPTION_TITLE)
            }
            if (bundle.containsKey(PlayOptionDiffCallback.ARGS_OPTION_ICON)) {
                binding.playerOptionIcon.setImageResource(bundle.getInt(PlayOptionDiffCallback.ARGS_OPTION_ICON))
            }
        }
    }
}