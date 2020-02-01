package com.seiko.player.service

import android.content.Context
import android.net.Uri
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.player.data.model.Progress
import com.seiko.player.util.VLCOptions
import kotlinx.coroutines.*
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.RendererItem
import org.videolan.libvlc.interfaces.ILibVLC
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.libvlc.interfaces.IMediaList
import org.videolan.libvlc.interfaces.IVLCVout
import org.videolan.libvlc.util.DisplayManager
import org.videolan.libvlc.util.VLCVideoLayout
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

class PlayerController(
    private val context: Context,
    private val libVLC: ILibVLC
) : IPlayerController,
    IVLCVout.Callback,
    MediaPlayer.EventListener {

    private val rendererDelegate = RendererDelegate(libVLC)

    private var mediaPlayer = newMediaPlayer()

    private val playbackState = AtomicInteger(PlaybackStateCompat.STATE_NONE)

    val progress by lazyAndroid { MutableLiveData<Progress>().apply { value = Progress() } }

    var seekable = false
    var pausable = false
    private var lastTime = 0L

    private var mediaPlayerEventListener: MediaPlayerEventListener? = null

    /**
     * Render
     */
    suspend fun init() {
        rendererDelegate.start()
    }

//    override fun getMediaPlayer(): MediaPlayer {
//        return mediaPlayer
//    }

    override fun attachView(surfaceFrame: View, displayManager: DisplayManager?) {
        if (surfaceFrame !is VLCVideoLayout) return
        val vlcVout = mediaPlayer.vlcVout
        if (vlcVout.areViewsAttached()) {
            vlcVout.detachViews()
        }
        mediaPlayer.attachViews(surfaceFrame, displayManager, true, false)
        mediaPlayer.videoScale = MediaPlayer.ScaleType.SURFACE_BEST_FIT
    }

    override fun detachView() {
        mediaPlayer.detachViews()
    }

    override fun getRendererItem(): RendererItem? {
        return rendererDelegate.getRenderer()
    }

    fun isReleased(): Boolean {
        return mediaPlayer.isReleased
    }

    fun isPlaying(): Boolean {
        return playbackState.get() == PlaybackStateCompat.STATE_PLAYING
    }

    fun isVideoPlaying(): Boolean {
        return !mediaPlayer.isReleased && mediaPlayer.vlcVout.areViewsAttached()
    }

    fun canSwitchToVideo(): Boolean {
        return getVideoTracksCount() > 0
    }

    fun getVideoTracksCount(): Int {
        return if (!mediaPlayer.isReleased && mediaPlayer.hasMedia()) {
            mediaPlayer.videoTracksCount
        } else 0
    }

    private fun updateProgress(newTime: Long = -1L, newLength: Long = -1L) {
//        progress.value = progress.value?.apply {
//            if (newTime != -1L) time = newTime
//            if (newLength != -1L) length = newLength
//        }
    }

    private fun resetPlaybackState(time: Long, duration: Long) {
        seekable = true
        pausable = true
        lastTime = time
        updateProgress(time, duration)
    }

    private fun setPlaybackStopped() {
        playbackState.lazySet(PlaybackStateCompat.STATE_STOPPED)
        updateProgress(0L, 0L)
        lastTime = 0L
    }

    fun expand(): IMediaList? {
        return mediaPlayer.media?.let {
            mediaPlayer.setEventListener(null)
            val items = it.subItems()
            it.release()
            mediaPlayer.setEventListener(this@PlayerController)
            items
        }
    }

    fun addSlaves(slaves: List<IMedia.Slave>) {
        for (slave in slaves) {
            mediaPlayer.addSlave(slave.type, Uri.parse(slave.uri), false)
        }
    }

    suspend fun startPlayback(media: IMedia, listener: MediaPlayerEventListener?, time: Long) {
//        resetPlaybackState(time, media.duration)
//        mediaPlayerEventListener = listener
//        mediaPlayer.media = media
//        mediaPlayer.play()
        mediaPlayerEventListener = listener
        resetPlaybackState(time, media.duration)
        mediaPlayer.setEventListener(null)
        withContext(Dispatchers.IO) {
            if (!mediaPlayer.isReleased) {
                mediaPlayer.media = media.apply { parse() }
            }
        }
        mediaPlayer.setEventListener(this)
        if (!mediaPlayer.isReleased) {
            mediaPlayer.setEqualizer(VLCOptions.getEqualizerSetFromSettings(context))
            mediaPlayer.setVideoTitleDisplay(MediaPlayer.Position.Disable, 0)
            mediaPlayer.play()
        }
    }

    override fun setRate(rate: Float, save: Boolean) {
        if (mediaPlayer.isReleased) {
            return
        }
        mediaPlayer.rate = rate
        // save
    }

    override fun play(): Boolean {
        if (mediaPlayer.hasMedia() && !mediaPlayer.isReleased) {
            mediaPlayer.play()
            return true
        }
        return false
    }

    override fun pause(): Boolean {
        if (isPlaying() && mediaPlayer.hasMedia() && pausable) {
            mediaPlayer.pause()
            return true
        }
        return false
    }

    override fun stop(): Boolean {
        if (mediaPlayer.hasMedia() && !mediaPlayer.isReleased) {
            mediaPlayer.stop()
            return true
        }
        return false
    }

    override fun restart() {
        releasePlayer(mediaPlayer)
        mediaPlayer = newMediaPlayer()
    }

    override fun release() {
        rendererDelegate.stop()
        releasePlayer(mediaPlayer)
    }

    private fun releasePlayer(mediaPlayer: MediaPlayer) {
        mediaPlayer.setEventListener(null)
        if (isVideoPlaying()) {
            mediaPlayer.vlcVout.detachViews()
        }
        mediaPlayer.media?.run {
            setEventListener(null)
            release()
        }
        mediaPlayer.release()
        setPlaybackStopped()
    }

    private fun newMediaPlayer(): MediaPlayer {
        val mediaPlayer = MediaPlayer(libVLC)
        mediaPlayer.vlcVout.addCallback(this)
        return mediaPlayer
    }

    override fun onSurfacesCreated(vlcVout: IVLCVout?) {
        Timber.d("onSurfacesCreated")
    }

    override fun onSurfacesDestroyed(vlcVout: IVLCVout?) {
        Timber.d("onSurfacesDestroyed")
    }

    override fun onEvent(event: MediaPlayer.Event) {
        when (event.type) {
            MediaPlayer.Event.Playing -> {
                playbackState.lazySet(PlaybackStateCompat.STATE_PLAYING)
            }
            MediaPlayer.Event.Paused -> {
                playbackState.lazySet(PlaybackStateCompat.STATE_PAUSED)
            }
            MediaPlayer.Event.EncounteredError -> {
                setPlaybackStopped()
            }
            MediaPlayer.Event.PausableChanged -> {
                pausable = event.pausable
            }
            MediaPlayer.Event.SeekableChanged -> {
                seekable = event.seekable
            }
            MediaPlayer.Event.LengthChanged -> {
                updateProgress(newLength = event.lengthChanged)
            }
            MediaPlayer.Event.TimeChanged -> {
                val time = event.timeChanged
                if (abs(time - lastTime) > 950L) {
                    updateProgress(newTime = time)
                    lastTime = time
                }
            }
        }
        mediaPlayerEventListener?.onEvent(event)
    }

    interface MediaPlayerEventListener {
        fun onEvent(event: MediaPlayer.Event)
    }
}