package com.seiko.player.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PlayParam(
    val videoPath: String,
    val videoTitle: String
) : Parcelable {

    override fun hashCode(): Int {
        return videoPath.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        return other is PlayParam && videoPath == other.videoPath
    }
}