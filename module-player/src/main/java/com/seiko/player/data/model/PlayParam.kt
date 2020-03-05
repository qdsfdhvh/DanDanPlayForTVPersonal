package com.seiko.player.data.model

import android.os.Parcelable
import com.seiko.player.util.getVideoMd5
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
class PlayParam(
    val videoPath: String,
    val videoTitle: String
) : Parcelable {

    @IgnoredOnParcel
    val videoMd5 by lazy {
        File(videoPath).getVideoMd5()
    }

    override fun hashCode(): Int {
        return videoPath.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        return other is PlayParam && videoPath == other.videoPath
    }
}