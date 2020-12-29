package com.seiko.tv.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.seiko.tv.domain.bangumi.GetBangumiHistoryCountUseCase
import com.seiko.tv.domain.bangumi.GetBangumiHistoryUseCase

class BangumiHistoryViewModel @ViewModelInject constructor(
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