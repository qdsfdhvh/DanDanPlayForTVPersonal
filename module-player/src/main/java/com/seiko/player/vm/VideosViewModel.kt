package com.seiko.player.vm

import android.app.Application
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.seiko.player.data.prefs.PrefDataSource
import com.seiko.player.vlc.provider.MediaLibraryProvider
import com.seiko.player.vlc.provider.VideosProvider
import org.videolan.medialibrary.media.MediaLibraryItem

class VideosViewModel(
    app: Application,
    private val prefDataSource: PrefDataSource
) : MediaLibraryViewModel(app) {

    val provider = VideosProvider(null, null, app, this)

    override val providers: Array<MediaLibraryProvider<out MediaLibraryItem>>
            = arrayOf(provider)

    init {
        watchMedia()
    }

    val isInitVlcMedia = liveData(viewModelScope.coroutineContext) {
        emit(prefDataSource.isInitVlcMedia)
    }

    fun setHasInitVlcMedia() {
        prefDataSource.isInitVlcMedia = true
    }

}