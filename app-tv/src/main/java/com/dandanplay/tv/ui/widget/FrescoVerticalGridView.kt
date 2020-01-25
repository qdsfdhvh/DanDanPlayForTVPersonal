package com.dandanplay.tv.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.leanback.widget.VerticalGridView
import androidx.recyclerview.widget.RecyclerView
import com.dandanplay.tv.util.getImagePipeline

class FrescoVerticalGridView : VerticalGridView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var mOldTime = 0L

//    private var preScrollState = RecyclerView.SCROLL_STATE_IDLE

    /**
     *  设置按键滚动的时间间隔.
     *  在小于time的间隔内消除掉.
     */
    var mTimeStamp = 150L

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

//    override fun onScrollStateChanged(state: Int) {
//        super.onScrollStateChanged(state)
//        when (state) {
//            //停止滑动
//            RecyclerView.SCROLL_STATE_IDLE -> {
//                if (getImagePipeline().isPaused) {
//                    getImagePipeline().resume()
//                }
//            }
//            RecyclerView.SCROLL_STATE_DRAGGING -> {
//                if (preScrollState == RecyclerView.SCROLL_STATE_SETTLING) {
//                    //触摸滑动不需要加载
//                    getImagePipeline().pause()
//                } else {
//                    //触摸滑动需要加载
//                    if (getImagePipeline().isPaused) {
//                        getImagePipeline().resume()
//                    }
//                }
//            }
//            //惯性滑动
//            RecyclerView.SCROLL_STATE_SETTLING -> {
//                getImagePipeline().pause()
//            }
//        }
//        preScrollState = state
//    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return false // 禁止滑动翻页
    }

}