package com.dandanplay.tv.vm

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dandanplay.tv.domain.SaveMagnetInfoUseCase
import com.seiko.common.data.ResultLiveData
import com.seiko.common.data.ResultData
import com.dandanplay.tv.domain.search.SearchBangumiListUseCase
import com.dandanplay.tv.domain.search.SearchMagnetListUseCase
import com.dandanplay.tv.data.db.model.ResMagnetItemEntity
import com.dandanplay.tv.model.api.SearchAnimeDetails
import com.seiko.common.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchBangumiViewModel(
    private val searchBangumiList: SearchBangumiListUseCase,
    private val searchMagnetList: SearchMagnetListUseCase,
    private val saveMagnetInfo: SaveMagnetInfoUseCase
) : ViewModel() {

    private val _mainState = ResultLiveData<Boolean>()
    val mainState: LiveData<ResultData<Boolean>> = _mainState

    private val _bangumiList = MutableLiveData<List<SearchAnimeDetails>>()
    val bangumiList: LiveData<List<SearchAnimeDetails>> = _bangumiList

    private val _magnetList = MutableLiveData<List<ResMagnetItemEntity>>()
    val magnetList: LiveData<List<ResMagnetItemEntity>> = _magnetList

    // 上一次搜搜的关键字
    private var query = ""

    // 当前点击的Magnet信息
    private var currentMagnetItem: ResMagnetItemEntity? = null

    /**
     * 搜索番剧和磁力链接
     * @param keyword 关键字
     */
    fun getBangumiListAndMagnetList(keyword: String) = viewModelScope.launch {
        query = keyword
        _mainState.showLoading()

        val defer1 = async(Dispatchers.IO) { searchBangumiList.invoke(keyword, "") }
        val defer2 = async(Dispatchers.IO) { searchMagnetList.invoke(keyword, -1, -1) }
        val result1 = defer1.await()
        val result2 = defer2.await()

        delay(200)

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