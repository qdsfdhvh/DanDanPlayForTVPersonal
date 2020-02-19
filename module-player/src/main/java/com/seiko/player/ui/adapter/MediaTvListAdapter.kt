package com.seiko.player.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.ui.adapter.BasePagedListAdapter
import com.seiko.common.util.scaleAnimator
import com.seiko.player.R
import com.seiko.player.databinding.MediaBrowserTvItemBinding
import com.seiko.player.ui.widget.FocusListener
import com.seiko.player.ui.widget.TvFocusableAdapter
import com.seiko.player.util.diff.MediaLibraryItemDiffCallback
import com.seiko.player.vlc.util.ImageLoader
import com.seiko.player.vlc.util.TvAdapterUtils
import org.videolan.medialibrary.Tools
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem

class MediaTvListAdapter(
    context: Context,
    private val imageLoader: ImageLoader
) : BasePagedListAdapter<MediaLibraryItem, MediaTvListAdapter.ViewHolder>(MediaLibraryItemDiffCallback()) {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MediaBrowserTvItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    private var focusListener: OnItemFocusListener? = null

    fun setOnItemFocusListener(listener: OnItemFocusListener?) {
        this.focusListener = listener
    }

    interface OnItemFocusListener {
        fun onItemFocused(view: View, item: MediaLibraryItem)
    }

    inner class ViewHolder(private val binding: MediaBrowserTvItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.isFocusable = true
            binding.root.isFocusableInTouchMode = true
            binding.root.setOnFocusChangeListener { v, hasFocus ->
                if (v == null) return@setOnFocusChangeListener
                v.scaleAnimator(hasFocus, 1.2f, 150)
                if (hasFocus) {
                    focusListener?.onItemFocused(v, getItem(layoutPosition)!!)
                }
            }
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != -1) {
                    listener?.onClick(this, getItem(position)!!, position)
                }
            }
            binding.container.clipToOutline =true
        }

        fun bind(position: Int) {
            val item = getItem(position)!!
            var progress = 0
            var resolution = ""
            var description = item.description
            var max = 0
            if (item is MediaWrapper) {
                if (item.type == MediaWrapper.TYPE_VIDEO) {
                    resolution = generateResolutionClass(item.width, item.height) ?: ""
                    description = if (item.time == 0L) Tools.millisToString(item.length) else Tools.getProgressText(item)
                    if (item.length > 0) {
                        val lastTime = item.displayTime
                        if (lastTime > 0) {
                            max = (item.length / 1000).toInt()
                            progress = (lastTime / 1000).toInt()
                        }
                    }
                }
            }
            binding.title.text = item.title
            binding.subtitle.text = description
            binding.badgeTV.text = resolution
            binding.progressBar.max = max
            binding.progressBar.progress = progress
            imageLoader.loadImage(binding.mediaCover, item)
        }
    }
}

private fun generateResolutionClass(width: Int, height: Int) : String? = if (width <= 0 || height <= 0) {
    null
} else when {
    width >= 7680 -> "8K"
    width >= 3840 -> "4K"
    width >= 1920 -> "1080p"
    width >= 1280 -> "720p"
    else -> "SD"
}