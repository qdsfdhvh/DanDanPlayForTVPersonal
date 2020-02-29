package com.seiko.player.vlc.media

import android.content.Context
import android.net.Uri
import org.videolan.libvlc.FactoryManager
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.ILibVLC
import org.videolan.libvlc.interfaces.ILibVLCFactory
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.libvlc.interfaces.IMediaFactory

class VlcInstance(
    private val context: Context,
    private val vlcOptions: VlcOptions
) {

    private val mediaFactory = FactoryManager.getFactory(IMediaFactory.factoryId) as IMediaFactory
    private val libVLCFactory = FactoryManager.getFactory(ILibVLCFactory.factoryId) as ILibVLCFactory

    private var libVlc: ILibVLC? = null

    @Synchronized
    private fun getLibVlc(): ILibVLC {
        if (libVlc == null) {
            libVlc = libVLCFactory.getFromOptions(context, vlcOptions.createOptions())
        }
        return libVlc!!
    }

    @Synchronized
    fun restart() {
        if (libVlc != null) {
            libVlc!!.release()
            libVlc = null
        }
    }

    fun newMediaPlayer(): MediaPlayer {
        return MediaPlayer(getLibVlc())
    }

    fun getFromUri(uri: Uri): IMedia {
        return mediaFactory.getFromUri(getLibVlc(), uri)
    }

}