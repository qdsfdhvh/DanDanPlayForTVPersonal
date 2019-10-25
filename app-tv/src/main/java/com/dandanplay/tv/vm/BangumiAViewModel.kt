package com.dandanplay.tv.vm

import android.util.SparseArray
import com.seiko.common.ResultLiveData
import com.seiko.common.BaseViewModel
import com.seiko.domain.entities.BangumiIntro
import com.seiko.domain.repository.BangumiRepository
import com.seiko.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class BangumiAViewModel(private val bangumiRepository: BangumiRepository): BaseViewModel() {

    val mainState = ResultLiveData<List<BangumiIntro>>()

    fun getBangumiList() = launch {
        mainState.showLoading()
        val result = withContext(Dispatchers.IO) {
            bangumiRepository.getBangumiList()
        }
        when(result) {
            is Result.Failure -> mainState.failed(result.exception)
            is Result.Success -> {
                val array = bangumiIntrosArray(result.data)
                mainState.success(array[getDayOfWeek()])
            }
        }
    }

    /**
     * 将动漫 分成 周日~周六7组
     */
    private suspend fun bangumiIntrosArray(intros: List<BangumiIntro>) = withContext(Dispatchers.Default) {
        val array = SparseArray<ArrayList<BangumiIntro>>(7)
        var airDay: Int
        var list: ArrayList<BangumiIntro>
        for (item in intros) {
            airDay = item.airDay
            if (array.indexOfKey(airDay) < 0) {
                list = ArrayList()
                array.put(airDay, list)
            } else {
                list = array[airDay]
            }
            list.add(item)
        }
        return@withContext array
    }

    /**
     * 今天周几
     * PS: 0代表周日，1-6代表周一至周六。
     */
    private fun getDayOfWeek(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
    }

}