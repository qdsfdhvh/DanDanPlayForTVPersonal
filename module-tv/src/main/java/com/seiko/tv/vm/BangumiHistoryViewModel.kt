package com.seiko.tv.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.domain.bangumi.GetBangumiHistoryFixedUseCase
import com.seiko.tv.domain.bangumi.GetBangumiHistoryUseCase
import kotlinx.coroutines.Dispatchers

class BangumiHistoryViewModel(
    private val getBangumiHistoryList: GetBangumiHistoryFixedUseCase
) : ViewModel() {

    /**
     * 我的历史，前20条
     */
    val historyBangumiList: LiveData<List<HomeImageBean>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(getBangumiHistoryList.invoke(200))
        }
}