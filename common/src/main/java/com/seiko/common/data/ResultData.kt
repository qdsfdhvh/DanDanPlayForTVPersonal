package com.seiko.common.data

import androidx.lifecycle.MutableLiveData

/**
 * A generic wrapper class around data request
 */
sealed class ResultData<out T : Any> {
    data class Loading(val state: Int = 0): ResultData<Nothing>()
    data class Success<out T : Any>(val data: T) : ResultData<T>()
    data class Error(val exception: Exception?): ResultData<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Loading -> "Loading[state=$state]"
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}

class ResultLiveData< T: Any> : MutableLiveData<ResultData<T>>() {

    fun showLoading() {
        value = ResultData.Loading()
    }

    fun success(data: T) {
        value = ResultData.Success(data)
    }

    fun failed(error: Exception?) {
        value = ResultData.Error(error)
    }

}
