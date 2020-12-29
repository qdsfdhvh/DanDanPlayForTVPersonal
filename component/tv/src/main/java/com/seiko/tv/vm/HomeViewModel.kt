package com.seiko.tv.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.seiko.common.data.Result
import com.seiko.tv.data.model.AirDayBangumiBean
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.domain.bangumi.GetBangumiFavoriteLiveDataUseCase
import com.seiko.tv.domain.bangumi.GetBangumiHistoryLiveDataUseCase
import com.seiko.tv.domain.bangumi.GetSeriesBangumiAirDayBeansUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.util.*

class HomeViewModel @ViewModelInject constructor(
    getWeekBangumiList: GetSeriesBangumiAirDayBeansUseCase,
    private val getBangumiFavoriteList: GetBangumiFavoriteLiveDataUseCase,
    private val getBangumiHistoryList: GetBangumiHistoryLiveDataUseCase
) : ViewModel() {

    /**
     * 每周更新
     */
    val weekBangumiList: LiveData<List<AirDayBangumiBean>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(getWeekBangumiList.invoke(getDayOfWeek())
                .flatMapConcat { result ->
                    flow {
                        when (result) {
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
    val favoriteList = liveData { emitSource(getBangumiFavoriteList.execute(20)) }

    /**
     * 我的历史（动态）
     */
    val historyBangumiList = liveData { emitSource(getBangumiHistoryList.execute(20)) }

}

/**
 * 今天周几
 * PS: 0代表周日，1-6代表周一至周六。
 */
private fun getDayOfWeek(): Int {
    return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
}