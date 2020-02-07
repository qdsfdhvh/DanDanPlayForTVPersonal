package com.seiko.torrent.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.ui.adapter.BaseAdapter
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentItemTitleBinding

typealias TabTitleConfigurationStrategy = (TabTitleAdapter.ViewHolder, Int) -> Unit

class TabTitleAdapter(
    private val size: Int,
    private val tabConfigurationStrategy: TabTitleConfigurationStrategy
) : BaseAdapter<TabTitleAdapter.ViewHolder>() {

    companion object {
        private const val ARGS_SELECT_POSITION = "ARGS_SELECT_POSITION"
    }

    private var selectPosition = 0

    /**
     * 当前选中的番剧
     */
    fun setSelectPosition(position: Int) {
        if (selectPosition == position) {
            return
        }
        if (selectPosition >= 0) {
            notifyItemChanged(selectPosition, Bundle().apply { putBoolean(ARGS_SELECT_POSITION, false) })
        }
        if (position >= 0) {
            notifyItemChanged(position, Bundle().apply { putBoolean(ARGS_SELECT_POSITION, true) })
        }
        selectPosition = position
    }

    override fun getItemCount(): Int {
        return size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TorrentItemTitleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        tabConfigurationStrategy.invoke(holder, position)
    }

    override fun onPayload(holder: ViewHolder, bundle: Bundle) {
        holder.payload(bundle)
    }

    inner class ViewHolder(
        private val binding: TorrentItemTitleBinding
    ) : RecyclerView.ViewHolder(binding.root)
        , View.OnFocusChangeListener
        , View.OnClickListener {

        init {
            binding.root.isFocusable = true
            binding.root.isFocusableInTouchMode = true
            binding.root.onFocusChangeListener = this
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when(v?.id) {
                R.id.container -> {
                    val position = adapterPosition
                    if (position <= 0) {
                        return
                    }
                    listener?.onClick(this, 0, position)
                }
            }
        }

        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if (v == null) return
            bind(adapterPosition)
        }

        fun bind(position: Int) {
            binding.root.isSelected = selectPosition == position
        }

        fun setText(text: CharSequence) {
            binding.title.text = text
        }

        fun payload(bundle: Bundle) {
            if (bundle.containsKey(ARGS_SELECT_POSITION)) {
                binding.root.isSelected = bundle.getBoolean(ARGS_SELECT_POSITION)
            }
        }

    }
}