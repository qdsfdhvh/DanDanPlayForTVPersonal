package com.seiko.player.media.vlc.control

import android.content.Context
import android.net.Uri
import com.seiko.player.media.option.VlcOptions
import org.videolan.libvlc.FactoryManager
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.ILibVLC
import org.videolan.libvlc.interfaces.ILibVLCFactory
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.libvlc.interfaces.IMediaFactory

class VlcLibManager(
    private val context: Context
) {

    private val mediaFactory = FactoryManager.getFactory(IMediaFactory.factoryId) as IMediaFactory
    private val libVLCFactory = FactoryManager.getFactory(ILibVLCFactory.factoryId) as ILibVLCFactory

    private var libVlc: ILibVLC? = null

    @Synchronized
    private fun getLibVlc(): ILibVLC {
        if (libVlc == null) {
            libVlc = libVLCFactory.getFromOptions(context, VlcOptions.createOptions(context))
        }
        return libVlc!!
    }

    @Synchronized
    fun release() {
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

    fun getEqualizerSetFromSettings(): MediaPlayer.Equalizer {
        return MediaPlayer.Equalizer.createFromPreset(0)
    }
}