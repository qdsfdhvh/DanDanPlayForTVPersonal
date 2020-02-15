package com.seiko.tv.data.model

data class HomeImageBean(
    val animeId: Long,
    val animeTitle: String,
    val imageUrl: String,
    val status: String
) {
    override fun toString(): String {
        return "HomeImageBean{" +
                "animeId='" + animeId + "'" +
                ",animeTitle='" + animeTitle + "'" +
                ",imageUrl='" + imageUrl + "'" +
                ",status='" + status + "'" +
                "}"
    }
}