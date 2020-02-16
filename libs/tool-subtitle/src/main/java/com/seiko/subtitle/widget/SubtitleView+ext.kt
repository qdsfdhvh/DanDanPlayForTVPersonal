package com.seiko.subtitle.widget

import com.seiko.subtitle.model.Subtitle

fun SubtitleView.setSubtitle(subtitle: Subtitle?, textSize: Int = 0) {
    if (subtitle == null) {
        clearSubtitle()
        return
    }

    val content = subtitle.content
    val contentArray = when {
        content.contains("\\N") -> content.split("\\N{ACKNOWLEDGE}".toRegex())
        content.contains("\n") -> content.split("\n")
        else -> listOf(content)
    }

    // 字幕为空，清楚上一次字幕
    if (contentArray.isEmptySubtitle()) {
        clearSubtitle()
        return
    }

    var isTopSubtitle = false
    val subtitleArray = contentArray.mapIndexed { i, s ->
        val subtitleText = SubtitleView.SubtitleText()
        subtitleText.setSize(textSize)

        val text = s.trim()
        //第一行以{开头，则认为是特殊字幕，现显示在顶部
        if (text.startsWith("{")) {
            if (i == 0) {
                isTopSubtitle = true
            }
            //忽略{}中内容
            val endIndex = text.lastIndexOf("}")
            if (endIndex != -1 && endIndex < text.length) {
                subtitleText.setText(text.substring(endIndex + 1))
            } else {
                subtitleText.setText(text)
            }
        } else {
            //普通内容显示在底部
            isTopSubtitle = false
            subtitleText.setText(text)
        }
        subtitleText
    }.toTypedArray()

    if (isTopSubtitle) {
        setTopTexts(subtitleArray)
    } else {
        setBottomTexts(subtitleArray)
    }
}

fun SubtitleView.clearSubtitle() {
    setTopAndBottomTexts(emptyArray(), emptyArray())
}

private fun List<String>.isEmptySubtitle(): Boolean {
    return isEmpty() || (size == 1 && get(0).isEmpty())
}