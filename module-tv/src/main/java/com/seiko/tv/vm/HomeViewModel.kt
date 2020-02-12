package com.seiko.tv.vm

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.seiko.tv.domain.bangumi.GetBangumiAirDayBeansUseCase
import com.seiko.tv.domain.GetFavoriteBangumiListUseCase
import com.seiko.tv.data.model.AirDayBangumiBean
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.common.data.Result
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class HomeViewModel(
    private val getWeekBangumiList: GetBangumiAirDayBeansUseCase,
    private val getFavoriteBangumiList: GetFavoriteBangumiListUseCase
): ViewModel() {

    /**
     * 每周更新
     */
    val weekBangumiList: LiveData<List<AirDayBangumiBean>> = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        when(val result = getWeekBangumiList.invoke(getDayOfWeek())) {
            is Result.Success -> emit(result.data)
            is Result.Error -> Timber.w(result.exception)
        }
    }

    /**
     * 今日更新
     */
    val todayBangumiList: LiveData<List<HomeImageBean>> = weekBangumiList.switchMap { data ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            val bangumiList = if (data.isNotEmpty()) data[0].bangumiList else emptyList()
            emit(bangumiList)
        }
    }

    /**
     * 我的收藏
     */
    val favoriteBangumiList: LiveData<PagedList<HomeImageBean>> = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emitSource(getFavoriteBangumiList.invoke())
    }

    /**
     * 今天周几
     * PS: 0代表周日，1-6代表周一至周六。
     */
    private fun getDayOfWeek(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
    }

}