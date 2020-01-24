package com.seiko.common.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.leanback.widget.VerticalGridView

class AppVerticalGridView : VerticalGridView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var mOldTime = 0L

    /**
     *  设置按键滚动的时间间隔.
     *  在小于time的间隔内消除掉.
     */
    var mTimeStamp = 280L

    /**
     *  用于优化按键快速滚动卡顿的问题.
     */
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event != null && event.repeatCount >= 2 && event.action == KeyEvent.ACTION_DOWN) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - mOldTime <= mTimeStamp) {
                return true
            }
            mOldTime = currentTime
        }
        return super.dispatchKeyEvent(event)
    }

//    override fun performClick(): Boolean {
//        return super.performClick()
//    }
//
//    override fun onTouchEvent(e: MotionEvent?): Boolean {
//        if (e?.action == MotionEvent.ACTION_DOWN) {
//            performClick()
//        }
//        return false // 禁止滑动翻页
//    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return false // 禁止滑动翻页
    }

}