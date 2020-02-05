package com.seiko.player.data.model

import android.net.Uri
import android.os.Parcelable
import com.seiko.player.util.constants.INVALID_VALUE
import kotlinx.android.parcel.Parcelize

@Parcelize
class PlayParam(
    val videoUri: Uri,
    var videoTitle: String = "",
    var hash: String = ""
) : Parcelable