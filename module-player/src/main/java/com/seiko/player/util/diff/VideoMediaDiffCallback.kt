package com.seiko.player.util.diff

import android.os.Bundle
import androidx.leanback.widget.DiffCallback
import com.seiko.player.data.db.model.VideoMedia
import com.seiko.player.data.model.PlayOption
import com.seiko.player.util.FileUtils

class VideoMediaDiffCallback : DiffCallback<VideoMedia>() {

    companion object {
        const val ARGS_VIDEO_TITLE = "ARGS_VIDEO_TITLE"
        const val ARGS_VIDEO_THUMBNAIL = "ARGS_VIDEO_THUMBNAIL"
    }

    override fun areItemsTheSame(oldItem: VideoMedia, newItem: VideoMedia): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: VideoMedia, newItem: VideoMedia): Boolean {
        return oldItem.videoPath == newItem.videoPath
                && oldItem.videoThumbnail == newItem.videoThumbnail
    }

    override fun getChangePayload(oldItem: VideoMedia, newItem: VideoMedia): Any? {
        val bundle = Bundle()
        if (oldItem.videoPath != newItem.videoPath) {
            bundle.putString(ARGS_VIDEO_TITLE, FileUtils.getFileName(newItem.videoPath))
        }
        if (oldItem.videoThumbnail != newItem.videoThumbnail) {
            bundle.putString(ARGS_VIDEO_THUMBNAIL, newItem.videoThumbnail)
        }
        return if (bundle.isEmpty) null else bundle
    }
}