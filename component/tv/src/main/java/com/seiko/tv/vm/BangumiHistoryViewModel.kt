package com.seiko.tv.vm

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.seiko.tv.domain.bangumi.GetBangumiHistoryCountUseCase
import com.seiko.tv.domain.bangumi.GetBangumiHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BangumiHistoryViewModel @Inject constructor(
    private val getBangumiHistoryCount: GetBangumiHistoryCountUseCase,
    private val getBangumiHistoryList: GetBangumiHistoryUseCase
) : ViewModel() {

    /**
     * 历史收藏的动漫数量
     */
    val bangumiCount: LiveData<Int> = liveData {
        emitSource(getBangumiHistoryCount.execute(200).asLiveData())
    }

    /**
     * 我的历史
     */
    fun loadData() = getBangumiHistoryList.execute(200).cachedIn(viewModelScope)
}