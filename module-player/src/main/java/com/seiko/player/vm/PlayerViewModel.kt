package com.seiko.player.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seiko.common.data.Result
import com.seiko.player.data.model.DanmaDownloadBean
import com.seiko.player.data.model.PlayParam
import com.seiko.player.data.prefs.PrefDataSource
import com.seiko.player.domain.GetDanmaUseCase
import com.seiko.player.domain.GetSubtitleUserCase
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayerViewModel(
    private val prefDataSource: PrefDataSource,
    private val getDanma: GetDanmaUseCase,
    private val getSubtitle: GetSubtitleUserCase
) : ViewModel() {

    /**
     * 弹幕资源
     */
    private val _danma = MutableLiveData<DanmaDownloadBean>()
    val danma: LiveData<DanmaDownloadBean> = _danma

    /**
     * 字幕路径
     */
    private val _subtitlePath = MutableLiveData<String>()
    val subtitlePath: LiveData<String> = _subtitlePath

    /**
     * 播放状态
     */
    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _showDanma = MutableLiveData<Boolean>()
    val showDanma: LiveData<Boolean> = _showDanma

    fun loadData() {
        _showDanma.value = prefDataSource.showDanma
    }

    /**
     * 下载视频 弹幕、字幕
     */
    fun downloadTracker(param: PlayParam) = viewModelScope.launch {
        when(val result = getDanma.invoke(param)) {
            is Result.Success -> {
                _danma.value = result.data
            }
            is Result.Error -> {
                Timber.e(result.exception)
            }
        }
        when(val result = getSubtitle.invoke(param)) {
            is Result.Success -> {
                _subtitlePath.value = result.data
            }
            is Result.Error -> {
                Timber.e(result.exception)
            }
        }
    }

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