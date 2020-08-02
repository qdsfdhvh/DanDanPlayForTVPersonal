package com.seiko.tv.domain.bangumi


import com.seiko.tv.data.model.AirDayBangumiBean
import com.seiko.tv.util.toHomeImageBean
import com.seiko.tv.data.db.model.BangumiIntroEntity
import com.seiko.common.data.Result
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 将连载动漫 分成 周日~周六7组
 * PS: 0代表周日，1-6代表周一至周六。
 */
@ActivityRetainedScoped
class GetSeriesBangumiAirDayBeansUseCase @Inject constructor(
    private val getBangumiList: GetSeriesBangumiListUseCase
) {

    /**
     * @param weekDay 已周几开头，输入0~6，假设输入二， 结果数据为：[周二、三、四、五、六、日、一]
     */
    operator fun invoke(weekDay: Int): Flow<Result<List<AirDayBangumiBean>>> {
        return getBangumiList.invoke().map { result ->
            when(result) {
                is Result.Success -> Result.Success(getAirDayBangumiBeans(weekDay, result.data))
                is Result.Error -> result
            }
        }.flowOn(Dispatchers.IO)
    }

}

private suspend fun getAirDayBangumiBeans(weekDay: Int, intros: List<BangumiIntroEntity>): List<AirDayBangumiBean> {
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
            weekDays[intro.airDay].bangumiList.add(intro.toHomeImageBean())
        }

        // 0 1 2 3 4 5 6
        // day = 3
        // 3 ~ 7, 0 ~ 3 = 3 4 5 6 0 1 2
        // day = 0
        // 0 ~ 7, 0 ~ 0 = 0 1 2 3 4 5 6
        // day = 6
        // 6 ~ 7, 0 ~ 6 = 6 0 1 2 3 4 5
        val descList = weekDays.subList(weekDay, 7) + weekDays.subList(0, weekDay)

        // day = 3
        // 3 2 1 0 6 5 4
        // day = 0
        // 0 6 5 4 3 2 1
        // day = 6
        // 6 5 4 3 2 1 0
        descList.subList(0, 1) + descList.subList(1, 7).asReversed()
    }
}