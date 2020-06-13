package com.seiko.tv.vm

import android.net.Uri
import androidx.lifecycle.*
import com.seiko.tv.domain.SaveMagnetInfoUseCase
import com.seiko.common.data.ResultData
import com.seiko.tv.domain.search.SearchBangumiListUseCase
import com.seiko.tv.domain.search.SearchMagnetListUseCase
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.data.model.api.SearchAnimeDetails
import com.seiko.common.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchBangumiViewModel(
    private val searchBangumiList: SearchBangumiListUseCase,
    private val searchMagnetList: SearchMagnetListUseCase,
    private val saveMagnetInfo: SaveMagnetInfoUseCase
) : ViewModel() {

    /**
     * 搜索关键字
     */
    val keyword = MutableLiveData<String>()
    private val changeKeyword = keyword.distinctUntilChanged()

    /**
     * 动漫结果
     */
    val bangumiList: LiveData<List<SearchAnimeDetails>> = changeKeyword.switchMap { keyword ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            when(val result = searchBangumiList.invoke(keyword, "")) {
                is Result.Error -> Timber.e(result.exception)
                is Result.Success ->  emit(result.data)
            }
        }
    }

    /**
     * 磁力结果
     */
    val magnetList: LiveData<List<ResMagnetItemEntity>> = changeKeyword.switchMap { keyword ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            when(val result = searchMagnetList.invoke(keyword, -1, -1)) {
                is Result.Error -> Timber.e(result.exception)
                is Result.Success ->  emit(result.data)
            }
        }
    }

    // 当前点击的Magnet信息
    private var currentMagnetItem: ResMagnetItemEntity? = null

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