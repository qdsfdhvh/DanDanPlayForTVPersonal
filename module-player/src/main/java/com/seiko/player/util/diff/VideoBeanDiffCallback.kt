package com.seiko.player.util.diff

import android.os.Bundle
import androidx.leanback.widget.DiffCallback
import androidx.recyclerview.widget.DiffUtil
import com.seiko.player.data.model.VideoBean

class VideoBeanDiffCallback : DiffCallback<VideoBean>() {

    companion object {
        const val ARGS_VIDEO_TITLE = "ARGS_VIDEO_TITLE"
        const val ARGS_VIDEO_THUMBNAIL = "ARGS_VIDEO_THUMBNAIL"
    }

    override fun areItemsTheSame(oldItem: VideoBean, newItem: VideoBean): Boolean {
        return oldItem.videoId == newItem.videoId
    }

    override fun areContentsTheSame(oldItem: VideoBean, newItem: VideoBean): Boolean {
        return oldItem.videoTitle == newItem.videoTitle
                && oldItem.videoThumbnail == newItem.videoThumbnail
    }

    override fun getChangePayload(oldItem: VideoBean, newItem: VideoBean): Any? {
        val bundle = Bundle()
        if (oldItem.videoTitle != newItem.videoTitle) {
            bundle.putString(ARGS_VIDEO_TITLE, newItem.videoTitle)
        }
        if (oldItem.videoThumbnail != newItem.videoThumbnail) {
            bundle.putString(ARGS_VIDEO_THUMBNAIL, newItem.videoThumbnail)
        }
        return if (bundle.isEmpty) null else bundle
    }
}