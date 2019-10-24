package com.dandanplay.tv2.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.text.SimpleDateFormat
import java.util.*


class TimeTextView : AppCompatTextView {

    private var timeReceiver = object : TimeBroadcastReceiver() {
        override fun run() {
            updateTime()
        }
    }

    private val sdf = SimpleDateFormat("HH:mm", Locale.US)

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (isInEditMode) return


        updateTime()
    }

    private fun updateTime() {
        text = sdf.format(Date(System.currentTimeMillis()))
    }

    override fun onAttachedToWindow() {
        timeReceiver.registerReceiver(context)
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        timeReceiver.unregisterReceiver(context)
        super.onDetachedFromWindow()
    }


}

abstract class TimeBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        if (action == Intent.ACTION_TIME_TICK) {
            run()
        }
    }

    abstract fun run()
}

private fun TimeBroadcastReceiver.registerReceiver(context: Context) {
    context.registerReceiver(this, IntentFilter(Intent.ACTION_TIME_TICK))
}

private fun TimeBroadcastReceiver.unregisterReceiver(context: Context) {
    context.unregisterReceiver(this)
}