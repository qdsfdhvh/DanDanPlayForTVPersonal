package com.seiko.tv.data.model

data class AirDayBangumiBean(
    val id: Int,
    val bangumiList: MutableList<HomeImageBean> = mutableListOf()
) {
    override fun toString(): String {
        return "AirDayBangumiBean{" +
                "id=$id," +
                "list=$bangumiList," +
                "}"
    }
}