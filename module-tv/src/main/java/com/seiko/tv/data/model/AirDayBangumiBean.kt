package com.seiko.tv.data.model

data class AirDayBangumiBean(
    val id: Int,
    val bangumiList: List<HomeImageBean> = ArrayList()
) {
    override fun toString(): String {
        return "AirDayBangumiBean{" +
                "id=$id," +
                "list=$bangumiList," +
                "}"
    }
}