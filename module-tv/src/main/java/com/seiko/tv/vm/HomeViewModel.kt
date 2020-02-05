package com.seiko.tv.vm

import androidx.lifecycle.*
import com.seiko.common.data.ResultLiveData
import com.seiko.common.data.ResultData
import com.seiko.common.data.Status
import com.seiko.tv.domain.bangumi.GetBangumiAirDayBeansUseCase
import com.seiko.tv.domain.GetFavoriteBangumiListUseCase
import com.seiko.tv.data.model.AirDayBangumiBean
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.common.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val _airDayBangumiList =
        ResultLiveData<List<AirDayBangumiBean>>()
    val airDayBangumiList: LiveData<ResultData<List<AirDayBangumiBean>>> = _airDayBangumiList
    val weekBangumiList: LiveData<ResultData<List<HomeImageBean>>> = Transformations.map(_airDayBangumiList) { data ->
        when(data.responseType) {
            Status.SUCCESSFUL -> ResultData(
                responseType = Status.SUCCESSFUL,
                data = data.data!![0].bangumiList
            )
            else -> ResultData(
                responseType = data.responseType,
                error = data.error
            )
        }
    }

    /**
     * 我的收藏
     */
    private val _favoriteBangumiList = MutableLiveData<List<HomeImageBean>>()
    val favoriteBangumiList: LiveData<List<HomeImageBean>> = _favoriteBangumiList

    /**
     * 获得当日更新动漫
     */
    fun getBangumiList(force: Boolean) = viewModelScope.launch {
        if (!force && airDayBangumiList.value != null) return@launch
        _airDayBangumiList.showLoading()
        val result = withContext(Dispatchers.IO) {
            getWeekBangumiList.invoke(getDayOfWeek())
        }
        when(result) {
            is Result.Success -> _airDayBangumiList.success(result.data)
            is Result.Error -> _airDayBangumiList.failed(result.exception)
        }
    }

    /**
     * 获得本地收藏
     */
    fun getFavoriteBangumiList() = viewModelScope.launch {
        when(val result = getFavoriteBangumiList.invoke()) {
            is Result.Success -> _favoriteBangumiList.value = result.data
            is Result.Error -> Timber.w(result.exception)
        }
    }

    /**
     * 今天周几
     * PS: 0代表周日，1-6代表周一至周六。
     */
    private fun getDayOfWeek(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
    }

}