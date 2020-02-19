package com.seiko.player.vm

import com.seiko.player.vlc.extensions.conflatedActor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import org.videolan.medialibrary.interfaces.Medialibrary


interface ICallBackHandler {
    val mediaLibrary : Medialibrary

    fun CoroutineScope.registerCallBacks(refresh: () -> Unit)
    fun releaseCallbacks()
    fun watchMedia()
    fun watchArtists()
    fun watchAlbums()
    fun watchGenres()
    fun watchPlaylists()
    fun watchHistory()
    fun watchMediaGroups()
}

class CallBackDelegate : ICallBackHandler,
    Medialibrary.OnMedialibraryReadyListener,
    Medialibrary.OnDeviceChangeListener,
    Medialibrary.MediaCb,
    Medialibrary.ArtistsCb,
    Medialibrary.AlbumsCb,
    Medialibrary.GenresCb,
    Medialibrary.PlaylistsCb, Medialibrary.HistoryCb, Medialibrary.MediaGroupCb {

    override val mediaLibrary = Medialibrary.getInstance()
    private lateinit var refreshActor: SendChannel<Unit>

    private var mediaCb = false
    private var artistsCb = false
    private var albumsCb = false
    private var genresCb = false
    private var playlistsCb = false
    private var historyCb = false
    private var mediaGroupsCb = false

    override fun CoroutineScope.registerCallBacks(refresh: () -> Unit) {
        refreshActor = conflatedActor { refresh() }
        mediaLibrary.addOnMedialibraryReadyListener(this@CallBackDelegate)
        mediaLibrary.addOnDeviceChangeListener(this@CallBackDelegate)
    }

    override fun watchMedia() {
        mediaLibrary.addMediaCb(this)
        mediaCb = true
    }

    override fun watchArtists() {
        mediaLibrary.addArtistsCb(this)
        artistsCb = true
    }

    override fun watchAlbums() {
        mediaLibrary.addAlbumsCb(this)
        albumsCb = true
    }

    override fun watchGenres() {
        mediaLibrary.addGenreCb(this)
        genresCb = true
    }

    override fun watchPlaylists() {
        mediaLibrary.addPlaylistCb(this)
        playlistsCb = true
    }

    override fun watchHistory() {
        mediaLibrary.addHistoryCb(this)
        historyCb = true
    }

    override fun watchMediaGroups() {
        mediaLibrary.addMediaGroupCb(this)
        mediaGroupsCb = true
    }

    override fun releaseCallbacks() {
        mediaLibrary.removeOnMedialibraryReadyListener(this)
        mediaLibrary.removeOnDeviceChangeListener(this)
        if (mediaCb) mediaLibrary.removeMediaCb(this)
        if (artistsCb) mediaLibrary.removeArtistsCb(this)
        if (albumsCb) mediaLibrary.removeAlbumsCb(this)
        if (genresCb) mediaLibrary.removeGenreCb(this)
        if (playlistsCb) mediaLibrary.removePlaylistCb(this)
        if (historyCb) mediaLibrary.removeHistoryCb(this)
        if (mediaGroupsCb) mediaLibrary.removeMediaGroupCb(this)
        refreshActor.close()
    }

    override fun onMedialibraryReady() { refreshActor.offer(Unit) }

    override fun onMedialibraryIdle() { refreshActor.offer(Unit) }

    override fun onDeviceChange() { refreshActor.offer(Unit) }

    override fun onMediaAdded() { refreshActor.offer(Unit) }

    override fun onMediaModified() { refreshActor.offer(Unit) }

    override fun onMediaDeleted() { refreshActor.offer(Unit) }

    override fun onArtistsAdded() { refreshActor.offer(Unit) }

    override fun onArtistsModified() { refreshActor.offer(Unit) }

    override fun onArtistsDeleted() { refreshActor.offer(Unit) }

    override fun onAlbumsAdded() { refreshActor.offer(Unit) }

    override fun onAlbumsModified() { refreshActor.offer(Unit) }

    override fun onAlbumsDeleted() { refreshActor.offer(Unit) }

    override fun onGenresAdded() { refreshActor.offer(Unit) }

    override fun onGenresModified() { refreshActor.offer(Unit) }

    override fun onGenresDeleted() { refreshActor.offer(Unit) }

    override fun onPlaylistsAdded() { refreshActor.offer(Unit) }

    override fun onPlaylistsModified() { refreshActor.offer(Unit) }

    override fun onPlaylistsDeleted() { refreshActor.offer(Unit) }

    override fun onHistoryModified() { refreshActor.offer(Unit) }

    override fun onMediaGroupsAdded() { refreshActor.offer(Unit) }

    override fun onMediaGroupsModified() { refreshActor.offer(Unit) }

    override fun onMediaGroupsDeleted() { refreshActor.offer(Unit) }
}
