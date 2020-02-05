package com.seiko.tv.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seiko.common.data.ResultData
import com.seiko.common.data.ResultLiveData
import com.seiko.tv.domain.bangumi.GetBangumiListWithSeasonUseCase
import com.seiko.tv.domain.bangumi.GetBangumiSeasonsUseCase
import com.seiko.tv.data.db.model.BangumiIntroEntity
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.common.data.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BangumiAreaViewModel(
    private val getBangumiSeasons: GetBangumiSeasonsUseCase,
    private val getBangumiListWithSeason: GetBangumiListWithSeasonUseCase
) : ViewModel() {

    private val _bangumiSeasons =
        ResultLiveData<List<BangumiSeason>>()
    val bangumiSeasons: LiveData<ResultData<List<BangumiSeason>>>
        get() = _bangumiSeasons

    private val _bangumiList =
        ResultLiveData<List<BangumiIntroEntity>>()
    val bangumiList: LiveData<ResultData<List<BangumiIntroEntity>>>
        get() = _bangumiList

    private var bangumiJob: Job? = null

    fun getBangumiSeasons(force: Boolean) = viewModelScope.launch {
        if (!force && _bangumiSeasons.value != null) return@launch
        _bangumiSeasons.showLoading()
        delay(50)
        when(val result = getBangumiSeasons.invoke()) {
            is Result.Success -> _bangumiSeasons.success(result.data)
            is Result.Error -> _bangumiSeasons.failed(result.exception)
        }
    }

    fun getBangumiListWithSeason(season: BangumiSeason, force: Boolean) {
        if (!force && _bangumiList.value != null) return
        if (bangumiJob != null && !bangumiJob!!.isCompleted) bangumiJob?.cancel()
        bangumiJob = viewModelScope.launch {
            _bangumiList.showLoading()
            delay(50)
            when(val result = getBangumiListWithSeason.invoke(season)) {
                is Result.Success -> _bangumiList.success(result.data)
                is Result.Error -> {
                    val error = result.exception
                    if (error !is CancellationException) {
                        _bangumiList.failed(error)
                    }
                }
            }
        }
    }

}