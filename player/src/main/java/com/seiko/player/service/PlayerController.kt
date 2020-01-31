package com.seiko.player.service

import android.content.Context
import android.net.Uri
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.player.data.db.Slave
import com.seiko.player.data.db.SlaveRepository
import com.seiko.player.data.model.Progress
import com.seiko.player.util.VLCInstance
import com.seiko.player.util.VLCOptions
import com.seiko.player.util.extensions.retry
import kotlinx.coroutines.*
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.RendererDiscoverer
import org.videolan.libvlc.RendererItem
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.libvlc.interfaces.IMediaList
import org.videolan.libvlc.interfaces.IVLCVout
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

class PlayerController(
    private val context: Context
) : IPlayerController,
    IVLCVout.Callback,
    MediaPlayer.EventListener {

    private val libVlc = VLCInstance.invoke(context)
    private val rendererDelegate = RendererDelegate(libVlc)

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
//    init {
//        launch {
//            rendererDelegate.startRender()
//        }
//    }


    suspend fun init() {
        rendererDelegate.startRender()
    }

    override fun getMediaPlayer(): MediaPlayer {
        return mediaPlayer
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
        progress.value = progress.value?.apply {
            if (newTime != -1L) time = newTime
            if (newLength != -1L) length = newLength
        }
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

    fun play(): Boolean {
        if (mediaPlayer.hasMedia() && !mediaPlayer.isReleased) {
            mediaPlayer.play()
            return true
        }
        return false
    }

    fun pause(): Boolean {
        if (isPlaying() && mediaPlayer.hasMedia() && pausable) {
            mediaPlayer.pause()
            return true
        }
        return false
    }

    fun stop(): Boolean {
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

    fun setRate(rate: Float, save: Boolean) {
        if (mediaPlayer.isReleased) {
            return
        }
        mediaPlayer.rate = rate
        // save
    }

    private fun newMediaPlayer(): MediaPlayer {
        val mediaPlayer = MediaPlayer(libVlc)
        mediaPlayer.setAudioDigitalOutputEnabled(false)
        val renderer = rendererDelegate.getRenderer()
        if (renderer != null) {
            mediaPlayer.setRenderer(renderer)
        }
        mediaPlayer.vlcVout.addCallback(this)
        return mediaPlayer
    }

    override fun onSurfacesCreated(vlcVout: IVLCVout?) {

    }

    override fun onSurfacesDestroyed(vlcVout: IVLCVout?) {

    }

    override fun onEvent(event: MediaPlayer.Event) {
        when (event.type) {
            MediaPlayer.Event.Playing -> playbackState.lazySet(PlaybackStateCompat.STATE_PLAYING)
            MediaPlayer.Event.Paused -> playbackState.lazySet(PlaybackStateCompat.STATE_PAUSED)
            MediaPlayer.Event.EncounteredError -> setPlaybackStopped()
            MediaPlayer.Event.PausableChanged -> pausable = event.pausable
            MediaPlayer.Event.SeekableChanged -> seekable = event.seekable
            MediaPlayer.Event.LengthChanged -> updateProgress(newLength = event.lengthChanged)
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