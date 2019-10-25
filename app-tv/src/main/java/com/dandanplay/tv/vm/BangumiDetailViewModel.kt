package com.dandanplay.tv.vm

import com.seiko.common.ResultLiveData
import com.seiko.common.BaseViewModel
import com.seiko.domain.entities.BangumiDetails
import com.seiko.domain.repository.BangumiRepository
import com.seiko.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BangumiDetailViewModel(private val bangumiRepository: BangumiRepository) : BaseViewModel() {

    val mainState = ResultLiveData<BangumiDetails>()

    fun getBangumiDetails(animeId: Int) = launch {
        mainState.showLoading()
        val result = withContext(Dispatchers.IO) {
            bangumiRepository.getBangumiDetails(animeId)
        }
        when(result) {
            is Result.Failure -> mainState.failed(result.exception)
            is Result.Success -> mainState.success(result.data)
        }
    }


}