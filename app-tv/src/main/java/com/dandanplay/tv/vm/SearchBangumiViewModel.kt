package com.dandanplay.tv.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.seiko.common.ResultLiveData
import com.seiko.common.BaseViewModel
import com.seiko.common.ResultData
import com.seiko.data.usecase.DownloadTorrentUseCase
import com.seiko.data.usecase.GetTorrentPathUseCase
import com.seiko.data.usecase.SearchBangumiListUseCase
import com.seiko.data.usecase.SearchMagnetListUseCase
import com.seiko.domain.entity.BangumiIntro
import com.seiko.domain.entity.ResMagnetItem
import com.seiko.domain.entity.SearchAnimeDetails
import com.seiko.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SearchBangumiViewModel(
    private val searchBangumiList: SearchBangumiListUseCase,
    private val searchMagnetList: SearchMagnetListUseCase,
    private val getTorrentPath: GetTorrentPathUseCase,
    private val downloadTorrent: DownloadTorrentUseCase
) : BaseViewModel() {

    private val _mainState = ResultLiveData<Boolean>()
    val mainState: LiveData<ResultData<Boolean>>
        get() = _mainState

    private val _bangumiList = MutableLiveData<List<SearchAnimeDetails>>()
    val bangumiList: LiveData<List<SearchAnimeDetails>>
        get() = _bangumiList

    private val _magnetList = MutableLiveData<List<ResMagnetItem>>()
    val magnetList: LiveData<List<ResMagnetItem>>
        get() = _magnetList

    private val _downloadState = ResultLiveData<String>()
    val downloadState: LiveData<ResultData<String>>
        get() = _downloadState

    // 上一次搜搜的关键字
    private var query = ""

    /**
     * 搜索番剧和磁力链接
     * @param keyword 关键字
     */
    fun getBangumiListAndMagnetList(keyword: String) = launch {
        query = keyword
        _mainState.showLoading()

        val defer1 = async(Dispatchers.IO) { searchBangumiList.invoke(keyword, "") }
        val defer2 = async(Dispatchers.IO) { searchMagnetList.invoke(keyword, -1, -1) }
        val result1 = defer1.await()
        val result2 = defer2.await()

        var error: Exception? = null
        when(result1) {
            is Result.Error -> error = result1.exception
            is Result.Success -> _bangumiList.value = result1.data
        }
        when(result2) {
            is Result.Error -> error = result2.exception
            is Result.Success -> _magnetList.value = result2.data
        }

        if (error != null) {
            _mainState.failed(error)
        } else {
            _mainState.success(true)
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
     * @param magnet 磁力链接 magnet:?xt=urn:btih:WEORDPJIJANN54BH2GNNJ6CSN7KB7S34
     */
    fun isTorrentExist(magnet: String): String {
        val torrentFile = File(getTorrentPath.invoke(SEARCH_TITLE, magnet))
        if (torrentFile.exists()) {
            return torrentFile.absolutePath ?: ""
        }
        return ""
    }

    /**
     * 下载种子
     * @param magnet 磁力链接
     */
    fun downloadTorrent(magnet: String) = launch {
        _downloadState.showLoading()
        val torrentPath = getTorrentPath.invoke(SEARCH_TITLE, magnet)
        val result = withContext(Dispatchers.IO) {
            downloadTorrent.invoke(torrentPath, magnet)
        }
        when(result) {
            is Result.Error -> _downloadState.failed(result.exception)
            is Result.Success -> _downloadState.success(torrentPath)
        }
    }

    companion object {
        // 直接搜索的种子的存放路径
        private const val SEARCH_TITLE = "SearchTorrent"
    }

}