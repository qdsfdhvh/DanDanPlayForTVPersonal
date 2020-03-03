package com.seiko.player.media.vlc.provider

import android.content.Context
import com.seiko.player.media.vlc.util.getAll
import org.videolan.medialibrary.interfaces.media.Folder
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.interfaces.media.VideoGroup

class VideosProvider(
    val folder: Folder?,
    val group: VideoGroup?,
    context: Context,
    model: SortableModel
) : MediaLibraryProvider<MediaWrapper>(context, model) {

    override fun canSortByFileNameName() = true
    override fun canSortByDuration() = true
    override fun canSortByLastModified() = folder == null

    override fun getTotalCount() = if (model.filterQuery == null) when {
        folder !== null -> folder.mediaCount(Folder.TYPE_FOLDER_VIDEO)
        group !== null -> group.mediaCount()
        else -> mediaLibrary.videoCount
    } else when {
        folder !== null -> folder.searchTracksCount(model.filterQuery, Folder.TYPE_FOLDER_VIDEO)
        group !== null -> group.searchTracksCount(model.filterQuery)
        else -> mediaLibrary.getVideoCount(model.filterQuery)
    }

    override fun getPage(loadSize: Int, startposition: Int): Array<MediaWrapper> {
        val list = if (model.filterQuery == null) when {
            folder !== null -> folder.media(Folder.TYPE_FOLDER_VIDEO, sort, desc, loadSize, startposition)
            group !== null -> group.media(sort, desc, loadSize, startposition)
            else -> mediaLibrary.getPagedVideos(sort, desc, loadSize, startposition)
        } else when {
            folder !== null -> folder.searchTracks(model.filterQuery, Folder.TYPE_FOLDER_VIDEO, sort, desc, loadSize, startposition)
            group !== null -> group.searchTracks(model.filterQuery, sort, desc, loadSize, startposition)
            else -> mediaLibrary.searchVideo(model.filterQuery, sort, desc, loadSize, startposition)
        }
        return list.also { completeHeaders(it, startposition) }
    }

    override fun getAll(): Array<MediaWrapper> = when {
        folder !== null -> folder.getAll(Folder.TYPE_FOLDER_VIDEO, sort, desc).toTypedArray()
        group !== null -> group.getAll(sort, desc).toTypedArray()
        else -> mediaLibrary.getVideos(sort, desc)
    }
}