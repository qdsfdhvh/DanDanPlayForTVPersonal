package com.dandanplay.tv.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.seiko.common.BaseViewModel
import com.seiko.common.ResultData
import com.seiko.common.ResultLiveData
import com.seiko.core.domain.bangumi.GetBangumiListWithSeasonUseCase
import com.seiko.core.domain.bangumi.GetBangumiSeasonsUseCase
import com.seiko.core.model.api.BangumiIntro
import com.seiko.core.model.api.BangumiSeason
import com.seiko.core.data.Result
import kotlinx.coroutines.launch

class BangumiAreaViewModel(
    private val getBangumiSeasons: GetBangumiSeasonsUseCase,
    private val getBangumiListWithSeason: GetBangumiListWithSeasonUseCase
) : BaseViewModel() {

    private val _bangumiSeasons = ResultLiveData<List<BangumiSeason>>()
    val bangumiSeasons: LiveData<ResultData<List<BangumiSeason>>>
        get() = _bangumiSeasons

    private val _bangumiList = ResultLiveData<List<BangumiIntro>>()
    val bangumiList: LiveData<ResultData<List<BangumiIntro>>>
        get() = _bangumiList

    fun getBangumiSeasons() = viewModelScope.launch {
        _bangumiSeasons.showLoading()
        when(val result = getBangumiSeasons.invoke()) {
            is Result.Success -> _bangumiSeasons.success(result.data)
            is Result.Error -> _bangumiSeasons.failed(result.exception)
        }
    }

    fun getBangumiListWithSeason(season: BangumiSeason) = viewModelScope.launch {
        _bangumiList.showLoading()
        when(val result = getBangumiListWithSeason.invoke(season)) {
            is Result.Success -> _bangumiList.success(result.data)
            is Result.Error -> _bangumiList.failed(result.exception)
        }
    }

}