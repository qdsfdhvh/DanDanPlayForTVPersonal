package com.seiko.module.torrent.vm

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.seiko.common.BaseViewModel
import com.seiko.data.helper.TorrentHelper
import com.seiko.data.usecase.torrent.GetTorrentTempWithContentUseCase
import com.seiko.data.usecase.torrent.GetTorrentTempWithNetUseCase
import com.seiko.domain.utils.Result
import com.seiko.module.torrent.constants.*
import com.seiko.module.torrent.ui.fragments.State
import com.seiko.torrent.models.TorrentMetaInfo
import kotlinx.coroutines.launch
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
     * 种子路径
     */
    private val _torrentMetaInfo = MutableLiveData<TorrentMetaInfo>()
    val torrentMetaInfo: LiveData<TorrentMetaInfo> = _torrentMetaInfo

    var saveTorrentFile = true

    fun loadData() {
        _downloadDir.value = torrentDownloadDir
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
                _state.value = State.FETCHING_MAGNET
                saveTorrentFile = false
                val magnetInfo = torrentHelper.fetchMagnet(uri.toString())
                _torrentMetaInfo.value = TorrentMetaInfo(magnetInfo)
                _state.value = State.FETCHING_MAGNET_COMPLETED
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

}