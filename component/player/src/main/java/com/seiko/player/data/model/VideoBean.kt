package com.seiko.player.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoBean(
    var videoId: Long = 0,
    var videoPath: String = "",
    var videoTitle: String = "",
    var videoThumbnail: String = ""
): Parcelable