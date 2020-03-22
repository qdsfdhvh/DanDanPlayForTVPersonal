package com.seiko.player.ui.helper

import android.annotation.SuppressLint
import android.os.Message
import android.view.MotionEvent
import android.view.View


/**
 *
 * @param initialInterval Initial interval in millis
 * @param normalInterval Normal interval in millis
 * @param clickListener The OnClickListener to trigger
 */
class OnRepeatListener(private val initialInterval: Int, private val normalInterval: Int, private val clickListener: View.OnClickListener) : View.OnTouchListener {
    private var downView: View? = null

    private val handler = OnRepeatHandler(this)

    init {
        if (initialInterval < 0 || normalInterval < 0)
            throw IllegalArgumentException("negative interval")
    }

    /**
     *
     * @param clickListener The OnClickListener to trigger
     */
    constructor(clickListener: View.OnClickListener) : this(DEFAULT_INITIAL_DELAY, DEFAULT_NORMAL_DELAY, clickListener)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                handler.removeMessages(ACTION_ONCLICK)
                handler.sendEmptyMessageDelayed(ACTION_ONCLICK, initialInterval.toLong())
                downView = view
                clickListener.onClick(view)
                view.isPressed = true
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler.removeMessages(ACTION_ONCLICK)
                downView = null
                view.isPressed = false
                return true
            }
        }
        return false
    }

    private class OnRepeatHandler(owner: OnRepeatListener) : WeakHandler<OnRepeatListener>(owner) {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                ACTION_ONCLICK -> {
                    sendEmptyMessageDelayed(ACTION_ONCLICK, owner!!.normalInterval.toLong())
                    owner!!.clickListener.onClick(owner!!.downView)
                }
            }
        }
    }

    companion object {

        private const val ACTION_ONCLICK = 0

        //Default values in milliseconds
        private const val DEFAULT_INITIAL_DELAY = 500
        private const val DEFAULT_NORMAL_DELAY = 150
    }
}