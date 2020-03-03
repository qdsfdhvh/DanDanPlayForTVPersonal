package com.seiko.player.media.vlc.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.*
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.Folder
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.interfaces.media.VideoGroup
import kotlin.math.min

const val MEDIA_LIBRARY_PAGE_SIZE = 500

@WorkerThread
fun Folder.getAll(type: Int = Folder.TYPE_FOLDER_VIDEO, sort: Int = Medialibrary.SORT_DEFAULT, desc: Boolean = false): List<MediaWrapper> {
    var index = 0
    val count = mediaCount(type)
    val all = mutableListOf<MediaWrapper>()
    while (index < count) {
        val pageCount = min(MEDIA_LIBRARY_PAGE_SIZE, count - index)
        val list = media(type, sort, desc, pageCount, index)
        all.addAll(list)
        index += pageCount
    }
    return all
}

@WorkerThread
fun VideoGroup.getAll(sort: Int = Medialibrary.SORT_DEFAULT, desc: Boolean = false): List<MediaWrapper> {
    var index = 0
    val count = mediaCount()
    val all = mutableListOf<MediaWrapper>()
    while (index < count) {
        val pageCount = min(MEDIA_LIBRARY_PAGE_SIZE, count - index)
        val list = media(sort, desc, pageCount, index)
        all.addAll(list)
        index += pageCount
    }
    return all
}