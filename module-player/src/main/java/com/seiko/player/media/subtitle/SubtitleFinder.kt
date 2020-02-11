package com.seiko.player.media.subtitle

import com.seiko.player.media.subtitle.model.Subtitle
import timber.log.Timber

object SubtitleFinder {

    fun find(position: Long, subtitles: List<Subtitle>?): Subtitle? {
        if (subtitles.isNullOrEmpty()) return null

        var start = 0
        var end = subtitles.size - 1
        var middle: Int
        while(start <= end) {
            middle = (start + end) / 2

            val middleSubtitle = subtitles[middle]

            when {
                position < middleSubtitle.start.mseconds -> {
                    if (position > middleSubtitle.end.mseconds) {
                        return middleSubtitle
                    }
                    end = middle - 1
                }
                position > middleSubtitle.end.mseconds -> {
                    if (position < middleSubtitle.start.mseconds) {
                        return middleSubtitle
                    }
                    start = middle + 1
                }
                position >= middleSubtitle.start.mseconds && position <= middleSubtitle.end.mseconds -> {
                    return middleSubtitle
                }
            }
        }
        return null
    }
}