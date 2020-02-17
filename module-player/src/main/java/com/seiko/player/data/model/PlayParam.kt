package com.seiko.player.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class PlayParam(
    val videoPath: String,
    val videoUri: Uri,
    val videoTitle: String
) : Parcelable {

    override fun hashCode(): Int {
        return videoUri.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        return other is PlayParam && videoUri == other.videoUri
    }
}