package com.dandanplay.tv.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.frostwire.jlibtorrent.TorrentInfo
import com.seiko.common.ResultLiveData
import com.seiko.common.BaseViewModel
import com.seiko.common.ResultData
import com.seiko.data.usecase.DownloadTorrentUseCase
import com.seiko.data.usecase.GetTorrentPathUseCase
import com.seiko.data.usecase.SearchMagnetListUseCase
import com.seiko.domain.pref.PrefHelper
import com.seiko.data.utils.DEFAULT_TORRENT_FOLDER
import com.seiko.domain.entity.ResMagnetItem
import com.seiko.domain.repository.SearchRepository
import com.seiko.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SearchMagnetViewModel(
    private val searchMagnetList: SearchMagnetListUseCase,
    private val getTorrentPath: GetTorrentPathUseCase,
    private val downloadTorrent: DownloadTorrentUseCase
) : BaseViewModel() {

    private val _mainState = ResultLiveData<List<ResMagnetItem>>()
    val mainState: LiveData<ResultData<List<ResMagnetItem>>>
        get() = _mainState

    private val _downloadState = ResultLiveData<String>()
    val downloadState: LiveData<ResultData<String>>
        get() = _downloadState

    // 上一次搜索的关键字
    private var query = ""

    /**
     * 搜索磁力链接
     * @param keyword 关键字
     */
    fun getMagnetListWithSearch(keyword: String) = launch {
        query = keyword
        _mainState.showLoading()
        val result = withContext(Dispatchers.IO) {
            searchMagnetList.invoke(keyword, -1, -1)
        }
        when(result) {
            is Result.Error -> _mainState.failed(result.exception)
            is Result.Success -> _mainState.success(result.data)
        }
    }

    /**
     * 关键字是否有变化
     */
    fun equalQuery(query: String): Boolean  {
        return this.query == query
    }

    /**
     * 是有存在此种子
     * @param animeTitle 番剧名称
     * @param magnet 磁力链接 magnet:?xt=urn:btih:WEORDPJIJANN54BH2GNNJ6CSN7KB7S34
     */
    fun isTorrentExist(animeTitle: String, magnet: String): String {
        val torrentFile = File(getTorrentPath.invoke(animeTitle, magnet))
        if (torrentFile.exists()) {
            return torrentFile.absolutePath ?: ""
        }
        return ""
    }

    /**
     * 下载种子
     * @param magnet 磁力链接
     */
    fun downloadTorrent(animeTitle: String, magnet: String) = launch {
        _downloadState.showLoading()
        val torrentPath = getTorrentPath.invoke(animeTitle, magnet)
        val result = withContext(Dispatchers.IO) {
            downloadTorrent.invoke(torrentPath, magnet)
        }
        when(result) {
            is Result.Error -> _downloadState.failed(result.exception)
            is Result.Success -> _downloadState.success(torrentPath)
        }
    }


}