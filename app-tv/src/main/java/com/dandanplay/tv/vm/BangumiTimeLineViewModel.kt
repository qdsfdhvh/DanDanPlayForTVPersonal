package com.dandanplay.tv.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.seiko.common.ResultLiveData
import com.seiko.common.BaseViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.data.usecase.bangumi.GetAirDayBangumiBeansUseCase
import com.seiko.domain.model.AirDayBangumiBean
import com.seiko.domain.model.BangumiIntro
import com.seiko.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class BangumiTimeLineViewModel(private val getWeekBangumiList: GetAirDayBangumiBeansUseCase): BaseViewModel() {

    private val _airDayBangumiList = ResultLiveData<List<AirDayBangumiBean>>()
    val airDayBangumiList: LiveData<ResultData<List<AirDayBangumiBean>>>
        get() = _airDayBangumiList

    val weekBangumiList: LiveData<ResultData<List<BangumiIntro>>> = Transformations.map(_airDayBangumiList) { data ->
        when(data.responseType) {
            Status.SUCCESSFUL -> ResultData(
                responseType = Status.SUCCESSFUL,
                data = data.data!![0].bangumiList)
            else -> ResultData(
                responseType = data.responseType,
                error = data.error)
        }
    }

    fun getBangumiList() = launch {
        _airDayBangumiList.showLoading()
        val result = withContext(Dispatchers.IO) {
            getWeekBangumiList.invoke(getDayOfWeek())
        }
        when(result) {
            is Result.Error -> _airDayBangumiList.failed(result.exception)
            is Result.Success -> {
                _airDayBangumiList.success(result.data)
            }
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