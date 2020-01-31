package com.seiko.player.util.extensions

import android.content.res.Resources

internal val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
internal val Int.px: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()