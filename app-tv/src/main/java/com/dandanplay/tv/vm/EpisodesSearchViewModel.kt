package com.dandanplay.tv.vm

import com.seiko.common.ResultLiveData
import com.seiko.common.BaseViewModel
import com.seiko.domain.entity.ResMagnetItem
import com.seiko.domain.repository.SearchRepository
import com.seiko.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EpisodesSearchViewModel(private val bangumiRepository: SearchRepository) : BaseViewModel() {

    val mainState = ResultLiveData<List<ResMagnetItem>>()

    private var query = ""

    fun getMagnetListWithSearch(keyword: String) = launch {
        query = keyword
        mainState.showLoading()
        val result = withContext(Dispatchers.IO) {
            bangumiRepository.getMagnetListWithSearch(keyword, -1, -1)
        }
        when(result) {
            is Result.Failure -> mainState.failed(result.exception)
            is Result.Success -> {
                mainState.success(result.data)
            }
        }
    }

    fun equalQuery(query: String): Boolean  {
        return this.query == query
    }

}