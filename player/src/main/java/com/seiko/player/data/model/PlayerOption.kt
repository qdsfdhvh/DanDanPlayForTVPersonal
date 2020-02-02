package com.seiko.player.data.model

import androidx.annotation.IntDef

data class PlayerOption(
    @PlayerOptionType val type: Int,
    val id: Int,
    val icon: Int,
    val title: String
) {
    @IntDef(PlayerOptionType.ADVANCED, PlayerOptionType.MEDIA_TRACKS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class PlayerOptionType {
        companion object {
            const val ADVANCED = 0
            const val MEDIA_TRACKS = 1
        }
    }
}