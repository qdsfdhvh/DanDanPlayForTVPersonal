package com.seiko.data.model.api

data class AirDayBangumiBean(
    val id: Int,
    val bangumiList: List<BangumiIntro> = ArrayList()
) {
    override fun toString(): String {
        return "AirDayBangumiBean{" +
                "id=$id," +
                "list=$bangumiList," +
                "}"
    }
}