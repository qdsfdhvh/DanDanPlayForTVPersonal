package com.seiko.player.media.vlc.interfaces

import org.videolan.medialibrary.interfaces.Medialibrary

interface SortModule {
    fun sort(sort: Int)
    fun canSortByName() = true
    fun canSortByFileNameName() = false
    fun canSortByDuration() = false
    fun canSortByInsertionDate() = false
    fun canSortByLastModified() = false
    fun canSortByReleaseDate() = false
    fun canSortByFileSize() = false
    fun canSortByArtist() = false
    fun canSortByAlbum ()= false
    fun canSortByPlayCount() = false
    fun canSortBy(sort: Int) = when (sort) {
        Medialibrary.SORT_DEFAULT -> true
        Medialibrary.SORT_ALPHA -> canSortByName()
        Medialibrary.SORT_FILENAME -> canSortByFileNameName()
        Medialibrary.SORT_DURATION -> canSortByDuration()
        Medialibrary.SORT_INSERTIONDATE -> canSortByInsertionDate()
        Medialibrary.SORT_LASTMODIFICATIONDATE -> canSortByLastModified()
        Medialibrary.SORT_RELEASEDATE -> canSortByReleaseDate()
        Medialibrary.SORT_FILESIZE -> canSortByFileSize()
        Medialibrary.SORT_ARTIST -> canSortByArtist()
        Medialibrary.SORT_ALBUM -> canSortByAlbum()
        Medialibrary.SORT_PLAYCOUNT -> canSortByPlayCount()
        else -> false
    }
}