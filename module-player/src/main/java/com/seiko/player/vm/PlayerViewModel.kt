package com.seiko.player.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seiko.common.data.Result
import com.seiko.player.data.model.DanmaDownloadBean
import com.seiko.player.data.model.PlayParam
import com.seiko.player.domain.DownloadDanmaUseCase
import kotlinx.coroutines.launch
import timber.log.Timber

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