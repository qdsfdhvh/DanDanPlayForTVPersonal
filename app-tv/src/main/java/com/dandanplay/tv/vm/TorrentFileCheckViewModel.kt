package com.dandanplay.tv.vm

import androidx.lifecycle.LiveData
import com.seiko.common.BaseViewModel
import com.seiko.common.ResultData
import com.seiko.common.ResultLiveData
import com.seiko.data.usecase.GetTorrentCheckBeanListUseCase
import com.seiko.data.usecase.GetTorrentLocalPlayUrlUseCase
import com.seiko.domain.entity.ThunderLocalUrl
import com.seiko.domain.entity.TorrentCheckBean
import com.seiko.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TorrentFileCheckViewModel(
    private val getTorrentCheckBeanList: GetTorrentCheckBeanListUseCase,
    private val getTorrentLocalPlayUrl: GetTorrentLocalPlayUrlUseCase
) : BaseViewModel() {

    private val _mainState = ResultLiveData<List<TorrentCheckBean>>()
    val mainState: LiveData<ResultData<List<TorrentCheckBean>>>
        get() = _mainState

    private val _thunderUrl = ResultLiveData<ThunderLocalUrl>()
    val thunderUrl: LiveData<ResultData<ThunderLocalUrl>>
        get() = _thunderUrl

    fun getTorrentCheckBeanList(torrentPath: String) = launch {
        _mainState.showLoading()
        val result = withContext(Dispatchers.Default) {
            getTorrentCheckBeanList.invoke(torrentPath)
        }
        when(result) {
            is Result.Success -> _mainState.success(result.data)
            is Result.Error -> _mainState.failed(result.exception)
        }
    }

    fun playForThunder(item: TorrentCheckBean, torrentPath: String) {
//        _thunderUrl.showLoading()
        val result = getTorrentLocalPlayUrl.invoke(item.index, item.size, torrentPath)
        when(result) {
            is Result.Success -> _thunderUrl.success(result.data)
            is Result.Error -> _thunderUrl.failed(result.exception)
        }
    }
}