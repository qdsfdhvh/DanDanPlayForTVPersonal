package com.seiko.player.vm

import androidx.lifecycle.*
import com.seiko.common.data.Result
import org.videolan.vlc.danma.DanmaResultBean
import com.seiko.player.data.model.PlayParam
import com.seiko.player.data.prefs.PrefDataSource
import com.seiko.player.domain.danma.GetDanmaResultUseCase
import com.seiko.player.domain.subtitle.GetSubtitleUseCase
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class PlayerViewModel(
    private val prefDataSource: PrefDataSource,
    private val getDanma: GetDanmaResultUseCase,
    private val getSubtitle: GetSubtitleUseCase
) : ViewModel() {

    val videoParam = MutableLiveData<PlayParam>()
    private val _videoParam = videoParam.distinctUntilChanged()
    /**
     * 弹幕资源
     */
    val danma: LiveData<DanmaResultBean> = _videoParam.switchMap { param ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            when(val result = getDanma.invoke(param)) {
                is Result.Success -> emit(result.data)
                is Result.Error -> Timber.w(result.exception)
            }
        }
    }

    /**
     * 字幕路径
     */
    val subtitlePath: LiveData<String> = _videoParam.switchMap { param ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            when(val result = getSubtitle.invoke(param)) {
                is Result.Success -> {
                    val subtitles = result.data
                    if (subtitles.isNotEmpty()) {
                        emit(subtitles[0])
                    }
                }
                is Result.Error -> {
                    Timber.e(result.exception)
                }
            }
        }
    }

    /**
     * 播放状态
     */
    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _showDanma = MutableLiveData<Boolean>().apply { value = prefDataSource.showDanma }
    val showDanma: LiveData<Boolean> get() = _showDanma

    /**
     * 播放
     */
    fun play() {
        _isPlaying.value = true
    }

    /**
     * 暂停
     */
    fun pause() {
       _isPlaying.value = false
    }

    /**
     * 切换播放状态
     */
    fun setVideoPlay() {
        if (_isPlaying.value == true) {
            pause()
        } else {
            play()
        }
    }

    /**
     * 切换弹幕显示
     */
    fun setDanmaShow() {
        val bool = !prefDataSource.showDanma
        _showDanma.value = bool
        prefDataSource.showDanma = bool
    }
}