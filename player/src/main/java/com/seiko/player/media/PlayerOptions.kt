package com.seiko.player.media

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import com.seiko.player.data.prefs.PrefDataSource
import com.seiko.player.util.getUri
import org.videolan.libvlc.FactoryManager
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.ILibVLC
import org.videolan.libvlc.interfaces.ILibVLCFactory
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.libvlc.interfaces.IMediaFactory
import org.videolan.libvlc.util.VLCUtil
import timber.log.Timber
import java.io.File
import kotlin.collections.ArrayList

class PlayerOptions(
    private val context: Context,
    private val pref: PrefDataSource
) {

    fun clear() {
        libVLC = null
    }

    fun newMediaPlayer(): MediaPlayer {
        return MediaPlayer(getLibVLC())
    }

    fun getRealUri(uri: Uri): Uri? {
        return getUri(context, uri)
    }

    fun getFromUri(uri: Uri): IMedia {
        val libVLCFactory = FactoryManager.getFactory(IMediaFactory.factoryId) as IMediaFactory
        return libVLCFactory.getFromUri(getLibVLC(), uri)
    }

    /**
     * 加载VLC配置
     */
    private var libVLC: ILibVLC? = null
    private fun getLibVLC(): ILibVLC {
        if (libVLC == null) {
            libVLC = createLibVLC()
        }
        return libVLC!!
    }

    private fun createLibVLC(): ILibVLC {
        val libVLCFactory = FactoryManager.getFactory(ILibVLCFactory.factoryId) as ILibVLCFactory
        return libVLCFactory.getFromOptions(context, createOptions())
    }

    private var audioTrackSessionId = 0
    private fun createOptions(): List<String> {
        val options = ArrayList<String>(50)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && audioTrackSessionId == 0) {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioTrackSessionId = audioManager.generateAudioSessionId()
        }

        options.add(if (pref.timeStretch)
            "--audio-time-stretch"
        else
            "--no-audio-time-stretch")
        options.add("--avcodec-skiploopfilter")
        options.add("" + getDeBlocking(pref.deBlocking))
        options.add("--avcodec-skip-frame")
        options.add(if (pref.enableFrameSkip) "2" else "0")
        options.add("--avcodec-skip-idct")
        options.add(if (pref.enableFrameSkip) "2" else "0")
        options.add("--subsdec-encoding")
        options.add(pref.subtitlesEncoding)
        options.add("--stats")
        if (pref.networkCaching > 0) {
            options.add("--network-caching=${pref.networkCaching}")
        }
        options.add("--android-display-chroma")
        options.add(pref.chromaFormat)
        options.add("--audio-resampler")
        options.add("soxr")
        options.add("--audiotrack-session-id=$audioTrackSessionId")

        options.add("--freetype-rel-fontsize=${pref.freeTypeRelFontSize}")
        if (pref.freeTypeBold) {
            options.add("--freetype-bold")
        }
        options.add("--freetype-color=${pref.freeTypeColor}")
        if (pref.freeTypeBackground) {
            options.add("--freetype-background-opacity=128")
        } else {
            options.add("--freetype-background-opacity=0")
        }
        if (pref.openGL == 1) {
            options.add("--vout=gles2,none")
        } else if (pref.openGL == 0) {
            options.add("--vout=android_display,none")
        }
        options.add("--keystore")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            options.add("file_crypt,none")
        } else {
            options.add("file_plaintext,none")
        }
        options.add("--keystore-file")
        options.add(File(context.getDir("keystore",
            Context.MODE_PRIVATE), "file").absolutePath)
        options.add(if (pref.enableVerboseMode) "-vv" else "-v")
        if (pref.castingPassThrough) {
            options.add("--sout-chromecast-audio-passthrough")
        } else {
            options.add("--no-sout-chromecast-audio-passthrough")
        }
        options.add("--sout-chromecast-conversion-quality=${pref.castingQuality}")
        options.add("--sout-keep")

        val customOptions = pref.customVLCOptions
        if (!customOptions.isNullOrBlank()) {
            val customOptionsList = customOptions.split("\\r?\\n".toRegex())
            options.addAll(customOptionsList)
        }
        options.add("--smb-force-v1")
        return options
    }
}

private fun getDeBlocking(deBlocking: Int): Int {
    var ret = deBlocking
    if (deBlocking < 0) {
        /**
         * Set some reasonable sDeblocking defaults:
         *
         * Skip all (4) for armv6 and MIPS by default
         * Skip non-ref (1) for all armv7 more than 1.2 Ghz and more than 2 cores
         * Skip non-key (3) for all devices that don't meet anything above
         */
        /**
         * Set some reasonable sDeblocking defaults:
         *
         * Skip all (4) for armv6 and MIPS by default
         * Skip non-ref (1) for all armv7 more than 1.2 Ghz and more than 2 cores
         * Skip non-key (3) for all devices that don't meet anything above
         */
        val m = VLCUtil.getMachineSpecs() ?: return ret
        if (m.hasArmV6 && !m.hasArmV7 || m.hasMips)
            ret = 4
        else if (m.frequency >= 1200 && m.processors > 2)
            ret = 1
        else if (m.bogoMIPS >= 1200 && m.processors > 2) {
            ret = 1
            Timber.d("Used bogoMIPS due to lack of frequency info")
        } else
            ret = 3
    } else if (deBlocking > 4) {
        // sanity check
        ret = 3
    }
    return ret
}