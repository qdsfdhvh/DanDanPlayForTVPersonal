package com.seiko.tv.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.domain.bangumi.GetBangumiHistoryFixedUseCase
import com.seiko.tv.util.constants.MAX_BANGUMI_HISTORY_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

class BangumiHistoryViewModel @ViewModelInject constructor(
    private val getBangumiHistoryList: GetBangumiHistoryFixedUseCase
) : ViewModel() {

    /**
     * 我的历史，前20条
     */
    val historyBangumiList: LiveData<List<HomeImageBean>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            delay(250)
            emit(getBangumiHistoryList.invoke(MAX_BANGUMI_HISTORY_SIZE))
        }
}