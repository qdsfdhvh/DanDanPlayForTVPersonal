package com.seiko.domain.entity

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