package com.dandanplay.tv2.widget

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dandanplay.tv2.R

class MainTitleLayout : FrameLayout {

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)

    private fun forceOn() {
        val count = childCount
        var view: View
        for (i in 0 until count) {
            view = getChildAt(i)
            view.isSelected = true
            if (view is ShadowTextView) {
                view.animate().scaleX(1.08F)
                    .scaleY(1.08F)
                    .setDuration(0L)
                    .start()
            }
        }

        if (getChildAt(0) is TextView) {
            setBackgroundResource(R.drawable.select_main)
        }
    }

    private fun forceOff() {
        val count = childCount
        var view: View
        for (i in 0 until count) {
            view = getChildAt(i)
            if (view is ShadowTextView) {
                view.animate().scaleX(1.0F)
                    .scaleY(1.0F)
                    .setDuration(0L)
                    .start()
            }
        }

        if (getChildAt(0) is TextView) {
            setBackgroundResource(0)
        }
    }

//    private fun backgroundForceOn() {
//        setBackgroundResource(R.drawable.select_main)
//    }
//
//    fun backgroundForceOff() {
//        setBackgroundResource(0)
//    }

    override fun setSelected(selected: Boolean) {
        if (selected != isSelected) {
            if (selected) {
                forceOn()
                val viewParent = parent
                if (viewParent is RecyclerView) {
                    val count = viewParent.childCount
                    for (i in 0 until count) {
                        val view = viewParent.getChildAt(i)
                        if (view != this) {
                            view.isSelected = false
                        }
                    }
                }
            } else {
                forceOff()
            }
        }

        if (selected && !isSelected) {
            Log.d("MainTitleLayout", "Select1")

        } else if(!isSelected) {
            Log.d("MainTitleLayout", "Select3")
//            forceOff()
        } else if (selected && isSelected) {
            Log.d("MainTitleLayout", "Select3")
//            backgroundForceOn()
        }

        super.setSelected(selected)
    }

}