package com.seiko.player.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PlayParam(
    val videoUri: Uri,
    var videoTitle: String = "",
    var hash: String = ""
) : Parcelable