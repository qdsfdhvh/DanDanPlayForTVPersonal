package com.seiko.data.usecase

import android.util.SparseArray
import com.seiko.domain.entity.BangumiIntro
import com.seiko.domain.repository.BangumiRepository
import com.seiko.domain.utils.Result
import com.seiko.domain.utils.exhaustive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 将动漫 分成 周日~周六7组
 * PS: 0代表周日，1-6代表周一至周六。
 */
class GetWeekBangumiListUseCase : KoinComponent {

    private val getBangumiListUseCase: GetBangumiListUseCase by inject()

    suspend operator fun invoke(): Result<SparseArray<ArrayList<BangumiIntro>>> {
        when(val result = getBangumiListUseCase.invoke()) {
            is Result.Success -> {
                return withContext(Dispatchers.Default) {
                    try {
                        val array = bangumiIntrosArray(result.data)
                        return@withContext Result.Success(array)
                    } catch (e: Exception) {
                        return@withContext Result.Error(e)
                    }
                }
            }
            is Result.Error -> {
                return result
            }
        }
    }

    private fun bangumiIntrosArray(intros: List<BangumiIntro>): SparseArray<ArrayList<BangumiIntro>> {
        val array = SparseArray<ArrayList<BangumiIntro>>(7)
        var airDay: Int
        var list: ArrayList<BangumiIntro>
        for (intro in intros) {
            airDay = intro.airDay
            if (array.indexOfKey(airDay) < 0) {
                list = ArrayList()
                array.put(airDay, list)
            } else {
                list = array[airDay]
            }
            list.add(intro)
        }
        return array
    }

}