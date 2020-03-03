package com.seiko.player.media.vlc.media

import android.net.Uri
import androidx.lifecycle.LiveData
import com.seiko.player.data.model.Progress
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.IMedia

interface IPlayerController {

    /**
     * 获取进度LiveData
     */
    fun getProgressLiveData(): LiveData<Progress>

    /**
     * 获取当前视频进度
     */
    fun getCurrentPosition(): Long

    /**
     * 获取当前视频长度
     */
    fun getCurrentDuration(): Long

    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean

    /**
     * 播放
     */
    fun play()

    /**
     * 暂停
     */
    fun pause(): Boolean

    /**
     * 停止
     */
    fun stop()

    /**
     * 跳转到
     */
    fun seekTo(position: Long)

    /**
     * 设置播放速度
     */
    fun setRate(rate: Float)

    /**
     * 设置播放源
     */
    suspend fun startPlayback(uri: Uri, listener: MediaPlayer.EventListener?, time: Long)

    /**
     * 注销播放器
     */
    suspend fun release()

}