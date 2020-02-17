package com.seiko.player.delegate

import android.view.MotionEvent
import androidx.leanback.widget.BaseGridView
import com.seiko.player.ui.video.VideoPlayerHandler

/**
 * 触控操作
 */
class VideoTouchDelegate(private val handler: VideoPlayerHandler) :
    BaseGridView.OnTouchInterceptListener {

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return when(event?.action) {
            MotionEvent.ACTION_UP -> {
                handler.controlShow()
                true
            }
            else -> false
        }
    }

}