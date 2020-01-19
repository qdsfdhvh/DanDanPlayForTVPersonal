package com.seiko.common.widget

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

//    companion object {
//        private const val MORE_STATE_END = 0 // 加载结束
//        private const val MORE_STATE_LOADING = 1 // 加载中
//        private const val MORE_STATE_NO_DATA = -1 // 加载更多已没有数据.
//    }

//    interface OnLoadMoreListener {
//        fun onLoadMore()
//        fun onLoadEnd()
//    }
//
//    private var mMoreState: Int = MORE_STATE_END
//    private var mListener: OnLoadMoreListener? = null
//
//    fun setOnLoadMoreListener(listener: OnLoadMoreListener?) {
//        mListener = listener
//    }

//    // 加载更多结束调用.
//    fun endMoreRefreshComplete() {
//        mMoreState = MORE_STATE_END
//    }
//
//    // 没有更多加载.
//    fun endRefreshingWithNoMoreData() {
//        mMoreState = MORE_STATE_NO_DATA
//    }

//    override fun onScrollStateChanged(state: Int) {
//        if (state == SCROLL_STATE_IDLE) {
//            adapter?.let { adapter ->
//                if (getLastVisiblePosition() >= adapter.itemCount - 1) {
//                    mListener?.let { listener ->
//                        if (mMoreState == MORE_STATE_END) {
//                            mMoreState = MORE_STATE_LOADING
//                            listener.onLoadMore()
//                        }
//                        if (mMoreState == MORE_STATE_NO_DATA) {
//                            listener.onLoadEnd()
//                        }
//                    }
//                }
//            }
//        }
//        super.onScrollStateChanged(state)
//    }
//
//    fun getLastVisiblePosition(): Int {
//        val childCount = childCount
//        if (childCount == 0) {
//            return 0
//        }
//        return getChildAdapterPosition(getChildAt(childCount - 1))
//    }
//
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