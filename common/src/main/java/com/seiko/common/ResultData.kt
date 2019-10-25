package com.seiko.common

import androidx.annotation.IntDef
import androidx.lifecycle.MutableLiveData

/**
 * A generic wrapper class around data request
 */
data class ResultData<T>(
    var responseType: Int,
    var data: T? = null,
    var error: Exception? = null) {
    override fun toString(): String {
        return "ResultData{" +
                "responseType=$responseType" +
                "error=$error" +
                "}"
    }
}

@IntDef(
    Status.SUCCESSFUL,
    Status.ERROR,
    Status.LOADING
)
@Retention(AnnotationRetention.SOURCE)
annotation class Status {
    companion object {
        const val SUCCESSFUL = 0
        const val ERROR = 1
        const val LOADING = 2
    }
}

class ResultLiveData<T> : MutableLiveData<ResultData<T>>() {

    fun showLoading() {
        value = ResultData(
            responseType = Status.LOADING
        )
    }

    fun success(data: T) {
        value = ResultData(
            responseType = Status.SUCCESSFUL,
            data = data
        )
    }

    fun failed(error: Exception?) {
        value = ResultData(
            responseType = Status.ERROR,
            error = error
        )
    }

}
