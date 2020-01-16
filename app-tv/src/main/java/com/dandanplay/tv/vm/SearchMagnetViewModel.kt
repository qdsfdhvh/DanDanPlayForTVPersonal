package com.dandanplay.tv.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dandanplay.tv.domain.SaveMagnetInfoUseCase
import com.seiko.common.ResultLiveData
import com.seiko.common.BaseViewModel
import com.seiko.common.ResultData
import com.seiko.core.domain.torrent.DownloadTorrentUseCase
import com.seiko.core.domain.torrent.GetTorrentInfoFileUseCase
import com.seiko.core.domain.search.SearchMagnetListUseCase
import com.seiko.core.data.db.model.ResMagnetItemEntity
import com.seiko.core.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SearchMagnetViewModel(
    private val searchMagnetList: SearchMagnetListUseCase,
    private val getTorrentInfoFile: GetTorrentInfoFileUseCase,
    private val downloadTorrent: DownloadTorrentUseCase,
    private val saveMagnetInfo: SaveMagnetInfoUseCase
) : BaseViewModel() {

    private val _mainState = ResultLiveData<List<ResMagnetItemEntity>>()
    val mainState: LiveData<ResultData<List<ResMagnetItemEntity>>> = _mainState

    private val _downloadState = ResultLiveData<File>()
    val downloadState: LiveData<ResultData<File>> = _downloadState

    // 上一次搜索的关键字
    private var query = ""

    // 当前点击的Magnet信息
    private var currentMagnetItem: ResMagnetItemEntity? = null

    /**
     * 搜索磁力链接
     * @param keyword 关键字
     */
    fun getMagnetListWithSearch(keyword: String) = viewModelScope.launch {
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
     * 选择当前的Magnet信息
     */
    fun setCurrentMagnetItem(item: ResMagnetItemEntity?) {
        currentMagnetItem = item
    }

    /**
     * 当前Magnet信息中的种子是否已经下载
     */
    fun isTorrentExist(): File? {
        val item = currentMagnetItem ?: return null
        val result = getTorrentInfoFile.invoke(item.magnet)
        if (result is Result.Success) {
            val torrentFile = result.data
            if (torrentFile.exists()) {
                return torrentFile
            }
        }
        return null
    }

    /**
     * 下载当前Magnet信息中的种子
     */
    fun downloadTorrent() = viewModelScope.launch {
        val item = currentMagnetItem ?: return@launch
        _downloadState.showLoading()
        delay(50)
        val result = withContext(Dispatchers.IO) {
            downloadTorrent.invoke(item.magnet)
        }
        when(result) {
            is Result.Error -> _downloadState.failed(result.exception)
            is Result.Success -> _downloadState.success(result.data)
        }
    }

    /**
     * 保存种子信息，尝试关联动漫与集数
     */
    fun saveMagnetInfoUseCase(hash: String, animeId: Long, episodeId: Int) = viewModelScope.launch {
        val item = currentMagnetItem ?: return@launch
        saveMagnetInfo.invoke(item, hash, animeId, episodeId)
    }
}