package com.seiko.common.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class TvViewPager : ViewPager {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        // 禁止翻页,将权限交给leanback来执行.
        return if (event!!.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
            || event.keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            false
        } else super.dispatchKeyEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            performClick()
        }
        return false // 禁止滑动翻页
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false // 禁止滑动翻页
    }

}