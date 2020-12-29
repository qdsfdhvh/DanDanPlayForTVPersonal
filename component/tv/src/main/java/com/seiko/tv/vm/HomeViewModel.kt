package com.seiko.tv.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.cachedIn
import com.seiko.tv.domain.bangumi.GetSeriesBangumiAirDayBeansUseCase
import com.seiko.tv.domain.bangumi.GetBangumiFavoriteUseCase
import com.seiko.tv.data.model.AirDayBangumiBean
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.common.data.Result
import com.seiko.tv.domain.bangumi.GetBangumiHistoryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.*

class HomeViewModel @ViewModelInject constructor(
    getWeekBangumiList: GetSeriesBangumiAirDayBeansUseCase,
    private val getFavoriteBangumiList: GetBangumiFavoriteUseCase,
    private val getBangumiHistoryList: GetBangumiHistoryUseCase
): ViewModel() {

    /**
     * 每周更新
     */
    val weekBangumiList: LiveData<List<AirDayBangumiBean>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(getWeekBangumiList.invoke(getDayOfWeek())
                .flatMapConcat { result ->
                    flow {
                        when(result) {
                            is Result.Success -> emit(result.data)
                            is Result.Error -> Timber.e(result.exception)
                        }
                    }
                }
                .asLiveData())
        }

    /**
     * 今日更新
     */
    val todayBangumiList: LiveData<List<HomeImageBean>> = weekBangumiList.switchMap { data ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (data.isNotEmpty()) {
                emit(data[0].bangumiList)
            } else {
                emit(emptyList<HomeImageBean>())
            }
        }
    }

    /**
     * 我的收藏（动态）
     */
    fun loadFavoriteList() = getFavoriteBangumiList.execute(10).cachedIn(viewModelScope)

    /**
     * 我的历史（动态），前20条
     */
    val historyBangumiList: LiveData<List<HomeImageBean>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(getBangumiHistoryList.invoke(10))
        }

}

/**
 * 今天周几
 * PS: 0代表周日，1-6代表周一至周六。
 */
private fun getDayOfWeek(): Int {
    return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
}