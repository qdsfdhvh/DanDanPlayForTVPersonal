package com.seiko.player.ui.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.ui.adapter.BaseAdapter
import com.seiko.common.ui.adapter.UpdatableAdapter
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.player.data.model.PlayerOption
import com.seiko.player.databinding.PlayerItemOptionBinding
import com.seiko.player.util.AppScope
import com.seiko.player.util.diff.PlayerOptionDiffCallback
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class OptionsAdapter(context: Context) : BaseAdapter<OptionsAdapter.OptionsViewHolder>(), UpdatableAdapter {

    private val inflater = LayoutInflater.from(context)
    private val diffCallback by lazyAndroid { PlayerOptionDiffCallback() }

    var items: List<PlayerOption> by Delegates.observable(emptyList()) { _, old, new ->
        update(old, new, diffCallback, false)
    }

    override fun getItemCount(): Int {
        return items.size
    }

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
                    listener?.onClick(this, items[position], position)
                }
            }
        }

        fun bind(position: Int) {
            val item = items[position]
            binding.playerOptionTitle.text = item.title
            binding.playerOptionIcon.setImageResource(item.icon)
        }

        fun payload(bundle: Bundle) {
            if (bundle.containsKey(PlayerOptionDiffCallback.ARGS_OPTION_TITLE)) {
                binding.playerOptionTitle.text = bundle.getString(PlayerOptionDiffCallback.ARGS_OPTION_TITLE)
            }
            if (bundle.containsKey(PlayerOptionDiffCallback.ARGS_OPTION_ICON)) {
                binding.playerOptionIcon.setImageResource(bundle.getInt(PlayerOptionDiffCallback.ARGS_OPTION_ICON))
            }
        }
    }
}