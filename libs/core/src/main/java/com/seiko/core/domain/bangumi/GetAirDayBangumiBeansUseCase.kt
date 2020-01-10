package com.seiko.core.domain.bangumi

import com.seiko.core.model.api.AirDayBangumiBean
import com.seiko.core.model.api.BangumiIntro
import com.seiko.core.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 将动漫 分成 周日~周六7组
 * PS: 0代表周日，1-6代表周一至周六。
 */
class GetAirDayBangumiBeansUseCase : KoinComponent {

    private val getBangumiListUseCase: GetBangumiListUseCase by inject()

    /**
     * @param weekDay 已周几开头，输入0~6，
     */
    suspend operator fun invoke(weekDay: Int): Result<List<AirDayBangumiBean>> {
        when(val result = getBangumiListUseCase.invoke()) {
            is Result.Success -> {
                val beans: List<AirDayBangumiBean>
                try {
                    beans = getAirDayBangumiBeans(weekDay, result.data)
                } catch (e: Exception) {
                    return Result.Error(e)
                }
                return Result.Success(beans)
            }
            is Result.Error -> {
                return result
            }
        }
    }

    private suspend fun getAirDayBangumiBeans(weekDay: Int, intros: List<BangumiIntro>): List<AirDayBangumiBean> {
        return withContext(Dispatchers.Default) {
            // 按顺序生成 周日 ~ 周六 数据
            val weekDays = listOf(
                AirDayBangumiBean(0),
                AirDayBangumiBean(1),
                AirDayBangumiBean(2),
                AirDayBangumiBean(3),
                AirDayBangumiBean(4),
                AirDayBangumiBean(5),
                AirDayBangumiBean(6)
            )

            // 导入动漫信息
            for (intro in intros) {
                (weekDays[intro.airDay].bangumiList as ArrayList).add(intro)
            }

            // 本周 ~ 周六 + 周日 ~ 本周
            weekDays.subList(weekDay, 7) + weekDays.subList(0, weekDay)
        }
    }

}