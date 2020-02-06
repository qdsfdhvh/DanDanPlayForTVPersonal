package com.seiko.player.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.FrameLayout
import androidx.leanback.widget.BaseGridView.OnKeyInterceptListener

class PlayerFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyle, defStyleRes) {

    private var mOnKeyInterceptListener: OnKeyInterceptListener? = null

    fun setOnKeyInterceptListener(listener: OnKeyInterceptListener) {
        mOnKeyInterceptListener = listener
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (mOnKeyInterceptListener?.onInterceptKeyEvent(event) == true) {
            return true
        }
        return super.dispatchKeyEvent(event)
    }

}