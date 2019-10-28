package com.seiko.domain.entity

data class ThunderLocalUrl(
    val taskId: Long,
    val title: String = "",
    val url: String = "",
    val error: Exception? = null
) {
    fun isSuccess() = taskId != -1L

    companion object {
        fun error(error: Exception?) = ThunderLocalUrl(-1L, error = error)
        fun success(taskId: Long, title: String, url: String) = ThunderLocalUrl(taskId, title, url)
    }
}