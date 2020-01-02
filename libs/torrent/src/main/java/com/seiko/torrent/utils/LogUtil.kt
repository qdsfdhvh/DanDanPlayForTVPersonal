package com.seiko.torrent.utils

import android.util.Log

var LOG_ENABLE = true

const val LOG_TAG = "TorrentDownload"

internal fun log(msg: String) {
    if (LOG_ENABLE) {
        Log.d(LOG_TAG, msg)
    }
}

internal fun log(msg: String, e: Throwable?) {
    if (LOG_ENABLE) {
        Log.e(LOG_TAG, msg, e)
    }
}

internal fun log(tag: String, msg: String) {
    if (LOG_ENABLE) {
        Log.d(tag, msg)
    }
}

internal fun log(tag: String, msg: String, e: Throwable?) {
    if (LOG_ENABLE) {
        Log.e(tag, msg, e)
    }
}
