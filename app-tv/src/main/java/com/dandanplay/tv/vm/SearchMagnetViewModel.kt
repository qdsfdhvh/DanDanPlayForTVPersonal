package com.dandanplay.tv.vm

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dandanplay.tv.domain.SaveMagnetInfoUseCase
import com.seiko.common.data.ResultLiveData
import com.seiko.common.data.ResultData
import com.dandanplay.tv.domain.search.SearchMagnetListUseCase
import com.dandanplay.tv.data.db.model.ResMagnetItemEntity
import com.seiko.common.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchMagnetViewModel(
    private val searchMagnetList: SearchMagnetListUseCase,
    private val saveMagnetInfo: SaveMagnetInfoUseCase
) : ViewModel() {

    private val _mainState =
        ResultLiveData<List<ResMagnetItemEntity>>()
    val mainState: LiveData<ResultData<List<ResMagnetItemEntity>>> = _mainState

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
     * 获取当前选择的磁力链接
     */
    fun getCurrentMagnetUri(): Uri? {
        val item = currentMagnetItem ?: return null
        return Uri.parse(item.magnet)
    }

    /**
     * 保存种子信息，尝试关联动漫与集数
     */
    fun saveMagnetInfoUseCase(hash: String, animeId: Long, episodeId: Int) = viewModelScope.launch {
        val item = currentMagnetItem ?: return@launch
        saveMagnetInfo.invoke(item, hash, animeId, episodeId)
    }
}