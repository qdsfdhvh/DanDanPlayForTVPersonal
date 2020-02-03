package com.seiko.player.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PlayParam(
    var videoTitle: String = "",
    val videoUri: Uri
) : Parcelable