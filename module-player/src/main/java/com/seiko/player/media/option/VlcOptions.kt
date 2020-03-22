package com.seiko.player.media.option

import android.content.Context
import android.media.AudioManager
import android.os.Build
import org.videolan.libvlc.util.HWDecoderUtil
import org.videolan.libvlc.util.VLCUtil
import timber.log.Timber
import java.io.File

/**
 * VLC配置
 */
object VlcOptions {

    private const val AOUT_AUDIOTRACK = 0
    private const val AOUT_OPENSLES = 1

    private var audioTrackSessionId = 0

    fun createOptions(context: Context): List<String> {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && audioTrackSessionId == 0) {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioTrackSessionId = audioManager.generateAudioSessionId()
        }

        val options = ArrayList<String>(50)
        val timeStretching = true
        val frameSkip = false
        val chroma = "RV16"
        val verboseMode = true

        val deBlocking = getDeblocking(-1)
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

    private fun getDeblocking(deblocking: Int): Int {
        var ret = deblocking
        if (deblocking < 0) {
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
        } else if (deblocking > 4) { // sanity check
            ret = 3
        }
        return ret
    }

    fun getAout(): String? {
        var aout = -1

        val hwaout = HWDecoderUtil.getAudioOutputFromDevice()
        if (hwaout == HWDecoderUtil.AudioOutput.AUDIOTRACK || hwaout == HWDecoderUtil.AudioOutput.OPENSLES)
            aout = if (hwaout == HWDecoderUtil.AudioOutput.OPENSLES) AOUT_OPENSLES else AOUT_AUDIOTRACK

        return if (aout == AOUT_OPENSLES) "opensles_android" else null /* audiotrack is the default */
    }

}