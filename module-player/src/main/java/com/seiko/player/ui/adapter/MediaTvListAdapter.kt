package com.seiko.player.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.ui.adapter.BasePagedListAdapter
import com.seiko.common.ui.adapter.FocusAnimator
import com.seiko.player.databinding.PlayerItemMediaBinding
import com.seiko.player.util.diff.MediaWrapperDiffCallback
import com.seiko.player.util.bitmap.ImageLoader
import org.videolan.medialibrary.Tools
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem

class MediaTvListAdapter(
    context: Context,
    private val imageLoader: ImageLoader,
    private val lifecycleScope: LifecycleCoroutineScope
) : BasePagedListAdapter<MediaWrapper, MediaTvListAdapter.ViewHolder>(MediaWrapperDiffCallback()) {

    private val inflater = LayoutInflater.from(context)
    var selectPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlayerItemMediaBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun submitList(pagedList: PagedList<MediaWrapper>?) {
        selectPosition = if (pagedList.isNullOrEmpty()) -1 else 0
        super.submitList(pagedList)
    }

    override fun submitList(pagedList: PagedList<MediaWrapper>?, commitCallback: Runnable?) {
        selectPosition = if (pagedList.isNullOrEmpty()) -1 else 0
        super.submitList(pagedList, commitCallback)
    }

    fun getSelectItem() = if (selectPosition == -1) null else getItem(selectPosition)

    private var focusListener: OnItemFocusListener? = null

    fun setOnItemFocusListener(listener: OnItemFocusListener?) {
        this.focusListener = listener
    }

    interface OnItemFocusListener {
        fun onItemFocused(view: View, item: MediaLibraryItem)
    }

    inner class ViewHolder(
        private val binding: PlayerItemMediaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val animator = FocusAnimator(binding.root, 1.2f,  false, 150)

        init {
            binding.root.isFocusable = true
            binding.root.isFocusableInTouchMode = true
            binding.root.setOnFocusChangeListener { _, hasFocus ->
                animator.animateFocus(hasFocus, false)
                if (hasFocus) {
                    val position = adapterPosition
                    if (position >= 0) {
                        selectPosition = position
                        focusListener?.onItemFocused(binding.root, getItem(position)!!)
                    }
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
            var description = ""
            var max = 0
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
            binding.title.text = item.title
            binding.time.text = description
            binding.badgeTV.text = resolution
            binding.progressBar.max = max
            binding.progressBar.progress = progress
            lifecycleScope.launchWhenStarted {
                imageLoader.loadImage(binding.mediaCover, item)
            }
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