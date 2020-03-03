package com.seiko.player.media.vlc.media

import android.net.Uri
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.player.data.model.Progress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.libvlc.interfaces.IVLCVout
import timber.log.Timber
import java.net.URI
import kotlin.math.abs

class VlcPlayerController(
    private val instance: VlcLibManager
): IPlayerController
    , IVLCVout.Callback
    , MediaPlayer.EventListener {

    /**
     * 播放器
     */
    private var _mediaPlayer: MediaPlayer? = null
    val mediaPlayer: MediaPlayer
        get() {
            if (_mediaPlayer == null) {
                _mediaPlayer = newMediaPlayer()
            }
            return _mediaPlayer!!
        }

    /**
     * 外部MediaListener
     */
    private var listener: MediaPlayer.EventListener? = null

    /**
     * 播放状态
     */
    private var playbackState = PlaybackStateCompat.STATE_STOPPED

    /**
     * 当前进度position
     */
    private var lastTime = 0L

    /**
     * 是否可拖动
     */
    var seekable = false
        private set

    /**
     * 是否可暂停
     */
    var pausable = false
        private set

    /**
     * 进度LiveData
     */
    private val progress by lazyAndroid { MutableLiveData<Progress>().apply { value = Progress() } }

    /**
     * 获取进度LiveData
     */
    override fun getProgressLiveData(): LiveData<Progress> {
        return progress
    }

    /**
     * 获取当前视频进度
     */
    override fun getCurrentPosition(): Long {
        return progress.value?.position ?: 0
    }

    /**
     * 获取当前视频长度
     */
    override fun getCurrentDuration(): Long {
        return progress.value?.duration ?: 0
    }

    /**
     * 是否正在播放
     */
    override fun isPlaying(): Boolean {
        return playbackState == PlaybackStateCompat.STATE_PLAYING
    }

    /**
     * 播放
     */
    override fun play() {
        if (mediaPlayer.hasMedia() && !mediaPlayer.isReleased) {
            mediaPlayer.play()
        }
    }

    /**
     * 暂停
     */
    override fun pause(): Boolean {
        if (pausable && isPlaying() && mediaPlayer.hasMedia() && !mediaPlayer.isReleased) {
            mediaPlayer.pause()
            return true
        }
        return false
    }

    /**
     * 停止
     */
    override fun stop() {
        if (mediaPlayer.hasMedia() && !mediaPlayer.isReleased) {
            mediaPlayer.stop()
            setPlaybackStopped()
        }
    }

    /**
     * 跳转到
     */
    override fun seekTo(position: Long) {
        if (seekable && mediaPlayer.hasMedia() && !mediaPlayer.isReleased) {
            mediaPlayer.time = position
        }
    }

    /**
     * 设置播放速度
     */
    override fun setRate(rate: Float) {
        if (!mediaPlayer.isReleased) {
            mediaPlayer.rate = rate
        }
    }

    /**
     * 设置播放源
     */
    override suspend fun startPlayback(uri: Uri, listener: MediaPlayer.EventListener?, time: Long) {
        this.listener = listener

        val media = instance.getFromUri(uri)
        media.addOption(":start-time=${time/1000L}")

        setPlaybackStarted(time, media.duration)

        mediaPlayer.setEventListener(null)
        withContext(Dispatchers.IO) {
            if (!mediaPlayer.isReleased) {
                val oldMedia = mediaPlayer.media
                if (oldMedia != null && !oldMedia.isReleased) {
                    oldMedia.release()
                }
                mediaPlayer.media = media
            }
        }
        mediaPlayer.setEventListener(this)

        if (!mediaPlayer.isReleased) {
            mediaPlayer.setVideoTitleDisplay(MediaPlayer.Position.Disable, 0)
            mediaPlayer.play()
        }
    }

    /**
     * 注销播放器
     */
    override suspend fun release() {
        instance.release()
        releasePlayer(_mediaPlayer)
        _mediaPlayer = null
        withContext(Dispatchers.Main) {
            setPlaybackStopped()
        }
    }

    /**
     * 更新进度
     */
    @MainThread
    private fun updateProgress(
        position: Long = progress.value?.position ?: 0,
        duration: Long = progress.value?.duration ?: 0
    ) {
        progress.value = progress.value?.apply {
            this.position = position
            this.duration = duration
        }
    }

    /**
     * 开始播放
     */
    @MainThread
    private fun setPlaybackStarted(position: Long, duration: Long) {
        seekable = true
        pausable = true
        lastTime = position
        updateProgress(position, duration)
    }

    /**
     * 停止播放，状态重置
     */
    @MainThread
    private fun setPlaybackStopped() {
        playbackState = PlaybackStateCompat.STATE_STOPPED
        updateProgress(0, 0)
        lastTime = 0
    }

    private fun newMediaPlayer(): MediaPlayer {
        val mediaPlayer = instance.newMediaPlayer()
        mediaPlayer.vlcVout.addCallback(this)
        return mediaPlayer
    }

    override fun onSurfacesCreated(vlcVout: IVLCVout?) {

    }

    override fun onSurfacesDestroyed(vlcVout: IVLCVout?) {

    }

    override fun onEvent(event: MediaPlayer.Event?) {
        when(event?.type) {
            MediaPlayer.Event.Playing -> playbackState = PlaybackStateCompat.STATE_PLAYING
            MediaPlayer.Event.Paused  -> playbackState = PlaybackStateCompat.STATE_PAUSED
            MediaPlayer.Event.EncounteredError -> setPlaybackStopped()
            MediaPlayer.Event.PausableChanged -> pausable = event.pausable
            MediaPlayer.Event.SeekableChanged -> seekable = event.seekable
            MediaPlayer.Event.LengthChanged -> {
                updateProgress(duration = event.lengthChanged)
            }
            MediaPlayer.Event.TimeChanged -> {
                val position = event.timeChanged
                if (abs(position - lastTime) > 950L) {
                    updateProgress(position = position)
                    lastTime = position
                }
            }
        }
        listener?.onEvent(event)
    }

    fun isVideoPlaying(): Boolean {
        return !mediaPlayer.isReleased && mediaPlayer.vlcVout.areViewsAttached()
    }

}

/**
 * 注销Vlc播放器
 */
private suspend fun releasePlayer(player: MediaPlayer?) {
    if (player == null || player.isReleased) return

    player.setEventListener(null)
    if (player.vlcVout.areViewsAttached()) {
        player.vlcVout.detachViews()
    }

    // TODO ERROR: VLCObject (org.videolan.libvlc.Media) finalized but not natively released
    val media = player.media
    if (media != null) {
        media.setEventListener(null)
        media.release()
        Timber.d("media is release=${media.isReleased}")
    }

    withContext(Dispatchers.IO) {
        player.release()
    }
}