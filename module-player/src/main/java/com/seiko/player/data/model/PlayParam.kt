package com.seiko.player.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class PlayParam(
    val videoUri: Uri,
    var videoTitle: String = "",
    var hash: String = ""
) : Parcelable {

    @IgnoredOnParcel
    val videoPath: String by lazy {
        if ("file".equals(videoUri.scheme, ignoreCase = true)) {
            videoUri.path!!
        } else {
            videoUri.toString()
        }
    }

    override fun hashCode(): Int {
        return videoUri.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        return other is PlayParam && videoUri == other.videoUri
    }
}