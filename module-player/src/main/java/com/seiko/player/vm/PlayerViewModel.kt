package com.seiko.player.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceDataStore
import com.seiko.common.data.Result
import com.seiko.player.data.model.DanmaDownloadBean
import com.seiko.player.data.model.PlayParam
import com.seiko.player.data.model.Progress
import com.seiko.player.domain.DownloadDanmaUseCase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.abs

class PlayerViewModel(private val downloadDanma: DownloadDanmaUseCase) : ViewModel() {

    private val _danma = MutableLiveData<DanmaDownloadBean>()
    val danma: LiveData<DanmaDownloadBean> = _danma

    fun downloadTracker(param: PlayParam) = viewModelScope.launch {
        when(val result = downloadDanma.invoke(param)) {
            is Result.Success -> {
                _danma.value = result.data
            }
            is Result.Error -> {
                Timber.e(result.exception)
            }
        }
    }

}