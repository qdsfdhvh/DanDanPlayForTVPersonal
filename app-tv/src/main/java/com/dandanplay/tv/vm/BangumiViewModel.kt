package com.dandanplay.tv.vm

import android.util.SparseArray
import androidx.lifecycle.LiveData
import com.seiko.common.ResultLiveData
import com.seiko.common.BaseViewModel
import com.seiko.common.ResultData
import com.seiko.data.usecase.GetBangumiListUseCase
import com.seiko.data.usecase.GetWeekBangumiListUseCase
import com.seiko.domain.entity.BangumiIntro
import com.seiko.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class BangumiViewModel(private val getWeekBangumiList: GetWeekBangumiListUseCase): BaseViewModel() {

    private val _mainState = ResultLiveData<List<BangumiIntro>>()
    val mainState: LiveData<ResultData<List<BangumiIntro>>>
        get() = _mainState

    fun getBangumiList() = launch {
        _mainState.showLoading()
        val result = withContext(Dispatchers.IO) {
            getWeekBangumiList.invoke()
        }
        when(result) {
            is Result.Error -> _mainState.failed(result.exception)
            is Result.Success -> {
//                val array = bangumiIntrosArray(result.data)
                _mainState.success(result.data[getDayOfWeek()])
            }
        }
    }

//    private suspend fun bangumiIntrosArray(intros: List<BangumiIntro>) = withContext(Dispatchers.Default) {
//        val array = SparseArray<ArrayList<BangumiIntro>>(7)
//        var airDay: Int
//        var list: ArrayList<BangumiIntro>
//        for (item in intros) {
//            airDay = item.airDay
//            if (array.indexOfKey(airDay) < 0) {
//                list = ArrayList()
//                array.put(airDay, list)
//            } else {
//                list = array[airDay]
//            }
//            list.add(item)
//        }
//        return@withContext array
//    }

    /**
     * 今天周几
     * PS: 0代表周日，1-6代表周一至周六。
     */
    private fun getDayOfWeek(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
    }

}