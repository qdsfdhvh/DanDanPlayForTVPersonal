package com.seiko.player.ui.card

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.seiko.common.ui.card.AbsCardView
import com.seiko.player.data.model.VideoBean
import com.seiko.player.databinding.MediaBrowserTvItemBinding
import com.seiko.player.util.diff.VideoBeanDiffCallback
import com.seiko.player.vlc.util.ImageLoader
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem
import timber.log.Timber

class MediaTvListCardView(context: Context, private val imageLoader: ImageLoader) : AbsCardView<MediaLibraryItem>(context) {

    private lateinit var binding: MediaBrowserTvItemBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = MediaBrowserTvItemBinding.inflate(inflater, parent, true)
    }

    override fun bind(item: MediaLibraryItem) {
        var progress = 0
//            var seen = 0L
//            var description = item.description
        var resolution = ""
        var max = 0
        if (item is MediaWrapper) {
            if (item.type == MediaWrapper.TYPE_VIDEO) {
                resolution = generateResolutionClass(item.width, item.height) ?: ""
//                    isSquare = false
//                    description = if (item.time == 0L) Tools.millisToString(item.length) else Tools.getProgressText(item)
//                    seen = item.seen
                if (item.length > 0) {
                    val lastTime = item.displayTime
                    if (lastTime > 0) {
                        max = (item.length / 1000).toInt()
                        progress = (lastTime / 1000).toInt()
                    }
                }
            }
        }
        binding.badgeTV.text = resolution
        binding.progressBar.max = max
        binding.progressBar.progress = progress
        imageLoader.loadImage(binding.mediaCover, item)
    }

    fun bind(bundle: Bundle) {

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