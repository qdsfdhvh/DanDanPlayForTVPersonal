package com.dandanplay.tv.vm

import com.seiko.common.ResultLiveData
import com.seiko.common.BaseViewModel
import com.seiko.domain.entity.SearchAnimeDetails
import com.seiko.domain.repository.SearchRepository
import com.seiko.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BangumiSearchViewModel(private val bangumiRepository: SearchRepository) : BaseViewModel() {

    val mainState = ResultLiveData<List<SearchAnimeDetails>>()

    fun getBangumiListWithSearch(keyword: String) = launch {
        mainState.showLoading()
        val result = withContext(Dispatchers.IO) {
            bangumiRepository.getBangumiListWithSearch(keyword, "")
        }
        when(result) {
            is Result.Failure -> mainState.failed(result.exception)
            is Result.Success -> mainState.success(result.data)
        }
    }


}