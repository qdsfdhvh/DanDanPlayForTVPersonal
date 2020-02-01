package com.seiko.player.delegate

import android.view.MotionEvent
import com.seiko.player.ui.VideoPlayerHandler

class VideoTouchDelegate(private val handler: VideoPlayerHandler) {

    fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_UP -> {
                handler.overlayShow()
            }
        }
        return true
    }
}