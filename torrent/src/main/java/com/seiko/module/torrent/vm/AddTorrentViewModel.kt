package com.seiko.module.torrent.vm

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.seiko.common.BaseViewModel
import com.seiko.common.eventbus.EventBusScope
import com.seiko.data.helper.TorrentHelper
import com.seiko.data.usecase.torrent.GetTorrentTempWithContentUseCase
import com.seiko.data.usecase.torrent.GetTorrentTempWithNetUseCase
import com.seiko.domain.utils.Result
import com.seiko.module.torrent.constants.*
import com.seiko.module.torrent.model.PostEvent
import com.seiko.module.torrent.ui.fragments.State
import com.seiko.torrent.model.MagnetInfo
import com.seiko.torrent.model.TorrentMetaInfo
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class AddTorrentViewModel(
    private val torrentHelper: TorrentHelper,
    private val torrentDownloadDir: File,
    private val getTorrentTempWithContentUseCase: GetTorrentTempWithContentUseCase,
    private val getTorrentTempWithNetUseCase: GetTorrentTempWithNetUseCase
) : BaseViewModel() {

    /**
     * 种子下载路径
     */
    private val _downloadDir = MutableLiveData<File>()
    val downloadDir: LiveData<File> = _downloadDir

    /**
     * 当前解析状态
     */
    private val _state = MutableLiveData<Int>()
    val state: LiveData<Int> = _state

    /**
     * 当前异常
     */
    private val _throwable = MutableLiveData<Throwable>()
    val throwable: LiveData<Throwable> = _throwable

    /**
     * 磁力信息
     */
    private val _magnetInfo = MutableLiveData<MagnetInfo>()
    val magnetInfo: LiveData<MagnetInfo> = _magnetInfo

    /**
     * 种子信息
     */
    private val _torrentMetaInfo = MutableLiveData<TorrentMetaInfo>()
    val torrentMetaInfo: LiveData<TorrentMetaInfo> = _torrentMetaInfo

    var saveTorrentFile = true

    fun loadData() {
        _downloadDir.value = torrentDownloadDir
        if (!EventBusScope.getDefault().isRegistered(this)) {
            EventBusScope.getDefault().register(this)
        }
    }

    fun decodeUri(uri: Uri) = launch {
        when(uri.scheme) {
            FILE_PREFIX -> {
                _state.value = State.DECODE_TORRENT_FILE
                _torrentMetaInfo.value = TorrentMetaInfo(uri.path!!)
                _state.value = State.DECODE_TORRENT_COMPLETED
            }
            CONTENT_PREFIX -> {
                _state.value = State.DECODE_TORRENT_FILE
                when(val result = getTorrentTempWithContentUseCase.invoke(uri)) {
                    is Result.Success -> {
                        saveTorrentFile = false
                        _torrentMetaInfo.value = TorrentMetaInfo(result.data)
                    }
                    is Result.Error -> _throwable.value = result.exception
                }
                _state.value = State.DECODE_TORRENT_COMPLETED
            }
            MAGNET_PREFIX -> {
                saveTorrentFile = false
                _magnetInfo.value = torrentHelper.fetchMagnet(uri.toString())

                _state.value = State.FETCHING_MAGNET
            }
            HTTP_PREFIX, HTTPS_PREFIX -> {
                _state.value = State.FETCHING_HTTP
                when(val result = getTorrentTempWithNetUseCase.invoke(uri.toString())) {
                    is Result.Success -> {
                        saveTorrentFile = false
                        _torrentMetaInfo.value = TorrentMetaInfo(result.data)
                    }
                    is Result.Error -> _throwable.value = result.exception
                }
                _state.value = State.FETCHING_HTTP_COMPLETED
            }
            else -> {
                _throwable.value = IllegalArgumentException("Unknown link/path type: ${uri.scheme}")
                return@launch
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceive(event: PostEvent) {
        when(event) {
            is PostEvent.MetaInfo -> {
                _state.value = State.FETCHING_MAGNET_COMPLETED
                val info = event.info
                LogUtils.d("接收TorrentMetaInfo：${info.torrentName}")

                _torrentMetaInfo.value = info
            }
        }
    }

    override fun onCleared() {
        if (EventBusScope.getDefault().isRegistered(this)) {
            EventBusScope.getDefault().unregister(this)
        }
        super.onCleared()
    }
}