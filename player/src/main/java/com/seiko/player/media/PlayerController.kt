package com.seiko.player.media

import android.content.Context
import android.net.Uri
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.player.data.model.Progress
import kotlinx.coroutines.*
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.ILibVLC
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.libvlc.interfaces.IVLCVout
import org.videolan.libvlc.util.DisplayManager
import org.videolan.libvlc.util.VLCVideoLayout
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

class PlayerController(
    private val options: PlayerOptions
) : IPlayerController,
    IVLCVout.Callback,
    MediaPlayer.EventListener {

    private var mediaPlayer: MediaPlayer? = null

    private val playbackState = AtomicInteger(PlaybackStateCompat.STATE_NONE)

    val progress by lazyAndroid { MutableLiveData<Progress>().apply { value = Progress() } }

    var seekable = false
    var pausable = false
    private var lastTime = 0L

    private var mediaPlayerEventListener: MediaPlayer.EventListener? = null

    override fun attachView(surfaceFrame: View, displayManager: DisplayManager?) {
        if (surfaceFrame !is VLCVideoLayout) return

        val mediaPlayer = getMediaPlayer()
        val vlcVout = mediaPlayer.vlcVout
        if (vlcVout.areViewsAttached()) {
            vlcVout.detachViews()
        }
        mediaPlayer.attachViews(surfaceFrame, displayManager, true, false)
        mediaPlayer.videoScale = MediaPlayer.ScaleType.SURFACE_BEST_FIT
    }

    override fun detachView() {
        if (isVideoPlaying()) {
            mediaPlayer!!.detachViews()
        }
    }

    fun canSwitchToVideo(): Boolean {
        return getVideoTracksCount() > 0
    }

    fun getVideoTracksCount(): Int {
        return if (!isReleased() && hasMedia()) {
            mediaPlayer!!.videoTracksCount
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

//    fun expand(): IMediaList? {
//        return mediaPlayer.media?.let {
//            mediaPlayer.setEventListener(null)
//            val items = it.subItems()
//            it.release()
//            mediaPlayer.setEventListener(this@PlayerController)
//            items
//        }
//    }

    fun addSlaves(slaves: List<IMedia.Slave>) {
        mediaPlayer?.run {
            for (slave in slaves) {
                addSlave(slave.type, Uri.parse(slave.uri), false)
            }
        }
    }

    suspend fun startPlayback(media: IMedia, listener: MediaPlayer.EventListener?, time: Long) {
        mediaPlayerEventListener = listener
        resetPlaybackState(time, media.duration)
        val mediaPlayer = getMediaPlayer()
        mediaPlayer.setEventListener(this)
        withContext(Dispatchers.IO) {
            if (!isReleased()) {
                mediaPlayer.media = media.apply { parse() }
            }
        }
        play()
    }

    override fun hasMedia(): Boolean {
        return mediaPlayer?.hasMedia() == true
    }

    fun isPlaying(): Boolean {
        return playbackState.get() == PlaybackStateCompat.STATE_PLAYING
    }

    fun isVideoPlaying(): Boolean {
        return !isReleased() && mediaPlayer?.vlcVout?.areViewsAttached() == true
    }

    override fun isReleased(): Boolean {
        return mediaPlayer == null || mediaPlayer!!.isReleased
    }

    override fun setRate(rate: Float, save: Boolean) {
        if (isReleased()) {
            return
        }
        mediaPlayer!!.rate = rate
    }

    override fun seekTo(position: Float) {
        if (!seekable || !hasMedia() || isReleased()) {
            return
        }
        mediaPlayer!!.position = position
    }

    override fun play(): Boolean {
        if (hasMedia() && !isReleased()) {
            Timber.d("play")
            mediaPlayer!!.play()
            return true
        }
        return false
    }

    override fun pause(): Boolean {
        if (isPlaying() && hasMedia() && pausable) {
            Timber.d("pause")
            mediaPlayer!!.pause()
            return true
        }
        return false
    }

    override fun stop(): Boolean {
        if (hasMedia() && !isReleased()) {
            mediaPlayer!!.stop()
            return true
        }
        return false
    }

    override fun release() {
        mediaPlayer?.let {
            releasePlayer(it)
            mediaPlayer = null
        }
    }

    private fun releasePlayer(mediaPlayer: MediaPlayer) {
        detachView()
        mediaPlayer.setEventListener(null)
        mediaPlayer.media?.run {
            setEventListener(null)
            release()
        }
        mediaPlayer.release()
        setPlaybackStopped()
    }

    private fun newMediaPlayer(): MediaPlayer {
        val mediaPlayer = options.newMediaPlayer()
        mediaPlayer.vlcVout.addCallback(this)
        return mediaPlayer
    }

    private fun getMediaPlayer(): MediaPlayer {
        if (mediaPlayer == null) {
            mediaPlayer = newMediaPlayer()
        }
        return mediaPlayer!!
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

}