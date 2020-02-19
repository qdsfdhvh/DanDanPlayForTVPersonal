package com.seiko.player.vm

import android.app.Application
import com.seiko.player.vlc.provider.MediaLibraryProvider
import com.seiko.player.vlc.provider.VideosProvider
import org.videolan.medialibrary.media.MediaLibraryItem

class VideosViewModel(
    app: Application
) : MediaLibraryViewModel(app) {

    val provider = VideosProvider(null, null, app, this)

    override val providers: Array<MediaLibraryProvider<out MediaLibraryItem>>
            = arrayOf(provider)

    init {
        watchMedia()
    }

}