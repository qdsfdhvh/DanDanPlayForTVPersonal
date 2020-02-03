package com.seiko.player.media.player

import androidx.annotation.IntDef
import tv.danmaku.ijk.media.player.IMediaPlayer

interface MediaPlayerCreatorFactory {

    fun getCreator(@Type type: Int): MediaPlayerCreator

    @IntDef(
        Type.IJK_ANDROID_PLAYER,
        Type.IJK_PLAYER,
        Type.EXO_PLAYER
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type {
        companion object {
            const val IJK_ANDROID_PLAYER = 1
            const val IJK_PLAYER = 2
            const val EXO_PLAYER = 3
        }
    }
}