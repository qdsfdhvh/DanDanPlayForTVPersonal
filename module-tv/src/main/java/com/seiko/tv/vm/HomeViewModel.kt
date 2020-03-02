package com.seiko.tv.vm

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.seiko.tv.domain.bangumi.GetSeriesBangumiAirDayBeansUseCase
import com.seiko.tv.domain.bangumi.GetBangumiFavoriteUseCase
import com.seiko.tv.data.model.AirDayBangumiBean
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.common.data.Result
import com.seiko.tv.domain.bangumi.GetBangumiHistoryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.*

class HomeViewModel(
    private val getWeekBangumiList: GetSeriesBangumiAirDayBeansUseCase,
    private val getFavoriteBangumiList: GetBangumiFavoriteUseCase,
    private val getBangumiHistoryList: GetBangumiHistoryUseCase
): ViewModel() {

    /**
     * 每周更新
     */
    val weekBangumiList: LiveData<List<AirDayBangumiBean>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            when(val result = getWeekBangumiList.invoke(getDayOfWeek())) {
                is Result.Success -> {
                    emit(result.data)
                }
                is Result.Error -> Timber.w(result.exception)
            }
        }

    /**
     * 今日更新
     */
    val todayBangumiList: LiveData<List<HomeImageBean>> = weekBangumiList.switchMap { data ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            val bangumiList = if (data.isNotEmpty()) data[0].bangumiList else emptyList()
//            delay(250)
            emit(bangumiList)
        }
    }

    /**
     * 我的收藏（动态）
     */
    val favoriteBangumiList: LiveData<PagedList<HomeImageBean>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
//            delay(250)
            emitSource(getFavoriteBangumiList.invoke(10))
        }

    /**
     * 我的历史（动态），前20条
     */
    val historyBangumiList: LiveData<PagedList<HomeImageBean>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            delay(250)
            emitSource(getBangumiHistoryList.invoke(10))
        }

    /**
     * 今天周几
     * PS: 0代表周日，1-6代表周一至周六。
     */
    private fun getDayOfWeek(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
    }

}