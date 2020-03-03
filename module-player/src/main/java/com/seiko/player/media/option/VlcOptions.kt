package com.seiko.player.media.option

import android.content.Context
import android.media.AudioManager
import android.os.Build
import java.io.File

/**
 * VLC配置
 */
object VlcOptions {

    private var audioTrackSessionId = 0

    fun createOptions(context: Context): List<String> {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && audioTrackSessionId == 0) {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioTrackSessionId = audioManager.generateAudioSessionId()
        }

        val options = ArrayList<String>(50)
        val timeStretching = false
        val frameSkip = false
        val chroma = "RV16"
        val verboseMode = true

        val deBlocking = -1
        val networkCaching = 0

        val subtitlesEncoding = ""
        val subtitlesSize = 16
        val subtitlesBold = false
        val subtitlesColor = "16777215"
        val subtitlesBackground = false

        val openGL = -1
        val castingPassThrough = false
        val castingQuality = 2

        options.add(if (timeStretching) {
            "--audio-time-stretch"
        } else {
            "--no-audio-time-stretch"
        })
        options.add("--avcodec-skiploopfilter")
        options.add(deBlocking.toString())
        options.add("--avcodec-skip-frame")
        options.add(if (frameSkip) "2" else "0")
        options.add("--avcodec-skip-idct")
        options.add(if (frameSkip) "2" else "0")
        options.add("--subsdec-encoding")
        options.add(subtitlesEncoding)
        options.add("--stats")
        if (networkCaching > 0) {
            options.add("--network-caching=$networkCaching")
        }
        options.add("--android-display-chroma")
        options.add(chroma)
        options.add("--audio-resampler")
        options.add("soxr")
        options.add("--audiotrack-session-id=$audioTrackSessionId")
        options.add("--freetype-rel-fontsize=$subtitlesSize")
        if (subtitlesBold) {
            options.add("--freetype-bold")
        }
        options.add("--freetype-color=$subtitlesColor")
        if (subtitlesBackground) {
            options.add("--freetype-background-opacity=128")
        } else {
            options.add("--freetype-background-opacity=0")
        }
        when(openGL) {
            1 -> {
                options.add("--vout=gles2,none")
            }
            0 -> {
                options.add("--vout=android_display,none")
            }
        }
        options.add("--keystore")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            options.add("file_crypt,none")
        } else {
            options.add("file_plaintext,none")
        }
        options.add("--keystore-file")
        options.add(File(context.getDir("keystore", Context.MODE_PRIVATE), "file").absolutePath)
        options.add(if (verboseMode) "-vv" else "-v")
        if (castingPassThrough) {
            options.add("--sout-chromecast-audio-passthrough")
        } else {
            options.add("--no-sout-chromecast-audio-passthrough")
        }
        options.add("--sout-chromecast-conversion-quality=$castingQuality")
        options.add("--sout-keep")
        options.add("--smb-force-v1")
        return options
    }

}