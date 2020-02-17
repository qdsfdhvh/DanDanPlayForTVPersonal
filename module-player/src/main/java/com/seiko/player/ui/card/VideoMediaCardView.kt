package com.seiko.player.ui.card

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.seiko.common.ui.card.AbsCardView
import com.seiko.common.util.loadFileImage
import com.seiko.common.util.loadImage
import com.seiko.player.data.db.model.VideoMedia
import com.seiko.player.databinding.PlayerItemMediaBinding
import com.seiko.player.util.FileUtils
import com.seiko.player.util.diff.VideoMediaDiffCallback
import timber.log.Timber

class VideoMediaCardView(context: Context) : AbsCardView<VideoMedia>(context) {

    private lateinit var binding: PlayerItemMediaBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = PlayerItemMediaBinding.inflate(inflater, parent, true)
    }

    override fun bind(item: VideoMedia) {
        Timber.d(item.videoThumbnail)
        binding.playerThumbnail.loadFileImage(item.videoThumbnail)
        binding.playerTitle.text = FileUtils.getFileName(item.videoPath)
    }

    fun bind(bundle: Bundle) {
        if (bundle.containsKey(VideoMediaDiffCallback.ARGS_VIDEO_THUMBNAIL)) {
            binding.playerThumbnail.loadFileImage(VideoMediaDiffCallback.ARGS_VIDEO_THUMBNAIL)
        }
        if (bundle.containsKey(VideoMediaDiffCallback.ARGS_VIDEO_TITLE)) {
            binding.playerTitle.text = bundle.getString(VideoMediaDiffCallback.ARGS_VIDEO_TITLE)
        }
    }

}