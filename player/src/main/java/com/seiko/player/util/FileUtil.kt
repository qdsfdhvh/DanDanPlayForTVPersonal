package com.seiko.player.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import org.videolan.libvlc.util.AndroidUtil

private const val SCHEME_CONTENT = "content"

fun getUri(context: Context, data: Uri?): Uri? {
    var uri = data
    if (uri != null && uri.scheme == SCHEME_CONTENT) {
        if (uri.authority == "media") {
            uri = getContentMediaUri(context, uri)
        }
    }
    return uri
}

fun getContentMediaUri(context: Context, data: Uri): Uri? {
    return context.contentResolver.query(
        data,
        arrayOf(MediaStore.Video.Media.DATA),
        null,
        null,
        null
    )?.use {
        val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        if (it.moveToFirst()) {
            AndroidUtil.PathToUri(it.getString(columnIndex)) ?: data
        } else data
    }
}