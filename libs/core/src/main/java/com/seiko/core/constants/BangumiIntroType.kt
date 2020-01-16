package com.seiko.core.constants

import androidx.annotation.IntDef

@IntDef(
    value = [
        BangumiIntroType.RELATED,
        BangumiIntroType.SIMILAR
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class BangumiIntroType {
    companion object {
        const val RELATED = 1
        const val SIMILAR = 2
    }
}