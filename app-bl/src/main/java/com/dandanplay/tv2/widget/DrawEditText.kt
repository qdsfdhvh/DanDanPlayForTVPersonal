package com.dandanplay.tv2.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

open class DrawEditText : AppCompatEditText, ForceListener {

    private lateinit var helper: ForceViewHelper

    private fun init(context: Context) {
        helper = ForceViewHelper(context, this)
    }

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init(context)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        helper.onDraw(canvas, 0, 0, measuredWidth, measuredHeight)
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