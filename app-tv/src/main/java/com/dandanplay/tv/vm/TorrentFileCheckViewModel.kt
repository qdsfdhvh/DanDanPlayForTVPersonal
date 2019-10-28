package com.dandanplay.tv.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.seiko.common.BaseViewModel
import com.seiko.common.ResultData
import com.seiko.common.ResultLiveData
import com.seiko.data.usecase.GetThunderLocalUrlUseCase
import com.seiko.data.usecase.GetTorrentCheckBeanListUseCase
import com.seiko.domain.entity.ThunderLocalUrl
import com.seiko.domain.entity.TorrentCheckBean
import com.seiko.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TorrentFileCheckViewModel(
    private val getTorrentCheckBeanList: GetTorrentCheckBeanListUseCase
) : BaseViewModel() {

    private val _mainState = ResultLiveData<List<TorrentCheckBean>>()
    val mainState: LiveData<ResultData<List<TorrentCheckBean>>>
        get() = _mainState

    fun getTorrentCheckBeanList(torrentPath: String) {
        _mainState.showLoading()
        when(val result = getTorrentCheckBeanList.invoke(torrentPath)) {
            is Result.Success -> _mainState.success(result.data)
            is Result.Error -> _mainState.failed(result.exception)
        }
    }
}