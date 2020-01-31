package org.videolan.liveplotgraph

import android.content.res.Resources

internal val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
//val Int.px: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()