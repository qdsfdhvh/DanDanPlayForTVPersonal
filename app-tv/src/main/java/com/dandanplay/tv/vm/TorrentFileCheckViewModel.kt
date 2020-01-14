package com.dandanplay.tv.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.seiko.common.BaseViewModel
import com.seiko.common.ResultData
import com.seiko.common.ResultLiveData
import com.seiko.core.domain.torrent.GetTorrentCheckBeanListUseCase
import com.seiko.core.domain.torrent.GetTorrentLocalPlayUrlUseCase
import com.seiko.core.model.ThunderLocalUrl
import com.seiko.core.model.TorrentCheckBean
import com.seiko.core.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TorrentFileCheckViewModel(
    private val getTorrentCheckBeanList: GetTorrentCheckBeanListUseCase,
    private val getTorrentLocalPlayUrl: GetTorrentLocalPlayUrlUseCase
) : BaseViewModel() {

    private val _mainState = ResultLiveData<List<TorrentCheckBean>>()
    val mainState: LiveData<ResultData<List<TorrentCheckBean>>> = _mainState

    private val _thunderUrl = ResultLiveData<ThunderLocalUrl>()
    val thunderUrl: LiveData<ResultData<ThunderLocalUrl>> = _thunderUrl

    fun getTorrentCheckBeanList(torrentPath: String, force: Boolean) = viewModelScope.launch {
        if (!force && mainState.value != null) return@launch
        _mainState.showLoading()
        delay(50) // 防止过快导致加载界面未被去除
        val result = withContext(Dispatchers.Default) {
            getTorrentCheckBeanList.invoke(torrentPath)
        }
        when(result) {
            is Result.Success -> _mainState.success(result.data)
            is Result.Error -> _mainState.failed(result.exception)
        }
    }

    fun playForThunder(torrentPath: String, item: TorrentCheckBean) = viewModelScope.launch {
        _thunderUrl.showLoading()
        delay(10) // 防止过快..
        val result = withContext(Dispatchers.Default) {
            getTorrentLocalPlayUrl.invoke(torrentPath, item.index, item.size)
        }
        when(result) {
            is Result.Success -> _thunderUrl.success(result.data)
            is Result.Error -> _thunderUrl.failed(result.exception)
        }
    }

    fun getTorrentTask(torrentPath: String, items: List<TorrentCheckBean>) {

    }

}