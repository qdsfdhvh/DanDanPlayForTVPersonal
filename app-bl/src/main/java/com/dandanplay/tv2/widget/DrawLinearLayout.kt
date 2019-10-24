package com.dandanplay.tv2.widget

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.LinearLayout

class DrawLinearLayout : LinearLayout, ForceListener {

    private lateinit var helper: ForceViewHelper

    constructor(context: Context): super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init(context)
    }

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    private fun init(context: Context) {
        setWillNotDraw(false)
        helper = ForceViewHelper(context, this)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (canvas != null)  {
            helper.onDraw(canvas, 0, 0, measuredWidth, measuredHeight)
        }
        super.dispatchDraw(canvas)
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        setUpEnabled(gainFocus)
    }

    fun setUpDrawable(drawable: Drawable) {
        helper.setUpDrawable(drawable)
    }

    override fun setUpDrawable(drawableId: Int) {
        helper.setUpDrawable(drawableId)
    }

    override fun setUpEnabled(bool: Boolean) {
        helper.setUpEnabled(bool)
    }

}