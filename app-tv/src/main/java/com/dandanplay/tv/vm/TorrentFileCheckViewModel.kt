package com.dandanplay.tv.vm

import androidx.lifecycle.LiveData
import com.seiko.common.BaseViewModel
import com.seiko.common.ResultData
import com.seiko.common.ResultLiveData
import com.seiko.data.usecase.torrent.GetTorrentCheckBeanListUseCase
import com.seiko.data.usecase.torrent.GetTorrentLocalPlayUrlUseCase
import com.seiko.data.usecase.torrent.GetTorrentTaskUseCase
import com.seiko.domain.entity.ThunderLocalUrl
import com.seiko.domain.entity.TorrentCheckBean
import com.seiko.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TorrentFileCheckViewModel(
    private val getTorrentCheckBeanList: GetTorrentCheckBeanListUseCase,
    private val getTorrentLocalPlayUrl: GetTorrentLocalPlayUrlUseCase,
    private val getTorrentTaskUseCase: GetTorrentTaskUseCase
) : BaseViewModel() {

    private val _mainState = ResultLiveData<List<TorrentCheckBean>>()
    val mainState: LiveData<ResultData<List<TorrentCheckBean>>>
        get() = _mainState

    private val _thunderUrl = ResultLiveData<ThunderLocalUrl>()
    val thunderUrl: LiveData<ResultData<ThunderLocalUrl>>
        get() = _thunderUrl

    fun getTorrentCheckBeanList(torrentPath: String) = launch {
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

    fun playForThunder(torrentPath: String, item: TorrentCheckBean) = launch {
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