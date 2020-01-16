package com.dandanplay.tv.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.seiko.common.ResultLiveData
import com.seiko.common.BaseViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.dandanplay.tv.domain.GetAirDayBangumiBeansUseCase
import com.dandanplay.tv.domain.GetFavoriteBangumiListUseCase
import com.dandanplay.tv.model.AirDayBangumiBean
import com.dandanplay.tv.model.HomeImageBean
import com.dandanplay.tv.util.toHomeImageBean
import com.seiko.core.data.db.model.BangumiIntroEntity
import com.seiko.core.data.Result
import com.seiko.core.data.db.model.BangumiDetailsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class HomeViewModel(
    private val getWeekBangumiList: GetAirDayBangumiBeansUseCase,
    private val getFavoriteBangumiList: GetFavoriteBangumiListUseCase
): BaseViewModel() {

    /**
     * 每周更新
     */
    private val _airDayBangumiList = ResultLiveData<List<AirDayBangumiBean>>()
    val airDayBangumiList: LiveData<ResultData<List<AirDayBangumiBean>>> = _airDayBangumiList
    val weekBangumiList: LiveData<ResultData<List<HomeImageBean>>> = Transformations.map(_airDayBangumiList) { data ->
        when(data.responseType) {
            Status.SUCCESSFUL -> ResultData(
                responseType = Status.SUCCESSFUL,
                data = data.data!![0].bangumiList)
            else -> ResultData(
                responseType = data.responseType,
                error = data.error)
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
            is Result.Error -> LogUtils.w(result.exception)
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