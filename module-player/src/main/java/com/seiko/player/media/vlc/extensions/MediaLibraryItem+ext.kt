package com.seiko.player.media.vlc.extensions

import org.videolan.medialibrary.interfaces.media.Album
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem

internal fun MediaLibraryItem.getDiscNumber(): String?  {
    return if (this is MediaWrapper && this.discNumber != 0) {
        "Disc ${this.discNumber}"
    } else null
}

internal fun MediaLibraryItem.getLength(): Long = when (itemType) {
    MediaLibraryItem.TYPE_ALBUM -> (this as Album).duration
    MediaLibraryItem.TYPE_MEDIA -> (this as MediaWrapper).length
    else -> 0L
}

internal fun MediaLibraryItem.getYear() = when (itemType) {
    MediaLibraryItem.TYPE_ALBUM -> if ((this as Album).releaseYear <= 0) "-" else releaseYear.toString()
    MediaLibraryItem.TYPE_MEDIA -> if ((this as MediaWrapper).releaseYear <= 0) "-" else releaseYear.toString()
    else -> "-"
}

internal fun MediaLibraryItem.isSpecialItem(): Boolean {
    return itemType == MediaLibraryItem.TYPE_ARTIST
            && (id == 1L || id == 2L) || itemType == MediaLibraryItem.TYPE_ALBUM
            && title == Album.SpecialRes.UNKNOWN_ALBUM
}
