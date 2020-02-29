package com.seiko.player.vlc.media

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.player.data.model.Progress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.libvlc.interfaces.IVLCVout
import kotlin.math.abs

class PlayerController(
    private val instance: VlcInstance
): IPlayerController
    , IVLCVout.Callback
    , MediaPlayer.EventListener {

    /**
     * 播放器
     */
    var mediaPlayer = newMediaPlayer()
        private set

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
    private var seekable = false

    /**
     * 是否可暂停
     */
    private var pausable = false

    /**
     * 进度LiveData
     */
    private val progress by lazyAndroid { MutableLiveData<Progress>() }

    /**
     * 获取进度LiveData
     */
    override fun getProgressLiveData(): LiveData<Progress> {
        return progress
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
    override suspend fun startPlayback(media: IMedia, listener: MediaPlayer.EventListener?, time: Long) {
        this.listener = listener

        setPlaybackStarted(time, media.duration)
        mediaPlayer.setEventListener(null)
        withContext(Dispatchers.IO) {
            if (!mediaPlayer.isReleased) {
                mediaPlayer.media = media
            }
        }
        mediaPlayer.setEventListener(this)
        if (!mediaPlayer.isReleased) {
            mediaPlayer.setVideoTitleDisplay(MediaPlayer.Position.Disable, 0)
        }
    }

    /**
     * 重启播放器
     */
    override suspend fun restart() {
        val oldMediaPlayer = mediaPlayer
        mediaPlayer = newMediaPlayer()
        releasePlayer(oldMediaPlayer)
    }

    /**
     * 注销播放器
     */
    override suspend fun release() {
        releasePlayer(mediaPlayer)
        setPlaybackStopped()
    }

    /**
     * 更新进度
     */
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
    private fun setPlaybackStarted(position: Long, duration: Long) {
        seekable = true
        pausable = true
        lastTime = position
        updateProgress(position, duration)
    }

    /**
     * 停止播放，状态重置
     */
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
            MediaPlayer.Event.LengthChanged -> updateProgress(duration = event.lengthChanged)
            MediaPlayer.Event.TimeChanged -> {
                val time = event.timeChanged
                if (abs(time - lastTime) > 950L) {
                    updateProgress(position = time)
                    lastTime = time
                }
            }
        }
        listener?.onEvent(event)
    }

    fun isVideoPlaying(): Boolean {
        return !mediaPlayer.isReleased && mediaPlayer.vlcVout.areViewsAttached()
    }
}

private suspend fun releasePlayer(player: MediaPlayer) {
    if (player.isReleased) return

    player.setEventListener(null)
    if (player.vlcVout.areViewsAttached()) {
        player.vlcVout.detachViews()
    }

    val media = player.media
    if (media != null) {
        media.setEventListener(null)
        media.release()
    }

    withContext(Dispatchers.IO) {
        player.release()
    }
}