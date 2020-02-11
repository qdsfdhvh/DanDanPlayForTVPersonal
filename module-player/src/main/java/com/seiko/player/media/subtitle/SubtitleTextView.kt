package com.seiko.player.media.subtitle

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Html
import android.util.AttributeSet
import android.widget.TextView
import com.seiko.player.media.subtitle.model.Subtitle

@SuppressLint("AppCompatCustomView")
class SubtitleTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : TextView(context, attrs, defStyleAttr, defStyleRes) {

    init {
        setShadowLayer(3f, 0f, 0f, Color.BLUE)
    }

    fun setSubtitle(subtitle: Subtitle?) {
        text = if (subtitle != null) Html.fromHtml(subtitle.content) else ""
    }

}