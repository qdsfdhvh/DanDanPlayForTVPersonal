package com.dandanplay.tv.vm

import androidx.lifecycle.LiveData
import com.seiko.common.ResultLiveData
import com.seiko.common.BaseViewModel
import com.seiko.common.ResultData
import com.seiko.data.usecase.torrent.DownloadTorrentUseCase
import com.seiko.data.usecase.torrent.GetTorrentInfoFileUseCase
import com.seiko.data.usecase.search.SearchMagnetListUseCase
import com.seiko.domain.model.api.ResMagnetItem
import com.seiko.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchMagnetViewModel(
    private val searchMagnetList: SearchMagnetListUseCase,
    private val getTorrentInfoFileUseCase: GetTorrentInfoFileUseCase,
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
        val result = getTorrentInfoFileUseCase.invoke(animeTitle, magnet)
        if (result is Result.Success) {
            val torrentFile = result.data
            if (torrentFile.exists()) {
                return torrentFile.absolutePath
            }
        }
        return ""
    }

    /**
     * 下载种子
     * @param magnet 磁力链接
     */
    fun downloadTorrent(animeTitle: String, magnet: String) = launch {
        _downloadState.showLoading()
        val result = withContext(Dispatchers.IO) {
            downloadTorrent.invoke(animeTitle, magnet)
        }
        when(result) {
            is Result.Error -> _downloadState.failed(result.exception)
            is Result.Success -> _downloadState.success(result.data)
        }
    }


}