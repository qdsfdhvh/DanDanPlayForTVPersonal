package com.seiko.player.ui.card

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.seiko.common.ui.card.AbsCardView
import com.seiko.common.util.loadFileImage
import com.seiko.player.data.model.VideoBean
import com.seiko.player.databinding.PlayerItemMediaBinding
import com.seiko.player.util.diff.VideoBeanDiffCallback
import timber.log.Timber

class VideoMediaCardView(context: Context) : AbsCardView<VideoBean>(context) {

    private lateinit var binding: PlayerItemMediaBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = PlayerItemMediaBinding.inflate(inflater, parent, true)
    }

    override fun bind(item: VideoBean) {
        Timber.d(item.videoThumbnail)
        binding.playerThumbnail.loadFileImage(item.videoThumbnail)
        binding.playerTitle.text = item.videoPath
    }

    fun bind(bundle: Bundle) {
        if (bundle.containsKey(VideoBeanDiffCallback.ARGS_VIDEO_THUMBNAIL)) {
            binding.playerThumbnail.loadFileImage(VideoBeanDiffCallback.ARGS_VIDEO_THUMBNAIL)
        }
        if (bundle.containsKey(VideoBeanDiffCallback.ARGS_VIDEO_TITLE)) {
            binding.playerTitle.text = bundle.getString(VideoBeanDiffCallback.ARGS_VIDEO_TITLE)
        }
    }

}