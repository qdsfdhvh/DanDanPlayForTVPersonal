package com.dandanplay.tv2.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlin.math.min

class ForceViewHelper(private val context: Context,
                      private val view: View): ForceListener {

    private val rect1 = Rect()
    private val rect2 = Rect()

    private var g = 0
    private var h = 0
    private var i = 0
    private var j = 0

    private var drawable: Drawable? = null
    private var isEnable = false

    private fun cycle() {
        val drawable = view.background
        if (drawable != null && drawable.getPadding(rect1)) {
            var rect = rect2
            rect.left -= min(rect1.left, view.paddingLeft)
            rect = rect2
            rect.right -= min(rect1.right, view.paddingRight)
            rect = rect2
            rect.top -= min(rect1.top, view.paddingTop)
            rect = rect2
            rect.bottom -= min(rect1.bottom, view.paddingBottom)
        }
    }

    fun onDraw(canvas: Canvas?, x: Int, y: Int, width: Int, height: Int) {
        if (canvas == null) return
        val drawable = this.drawable
        if (isEnable && drawable != null) {
            if (drawable.getPadding(rect2)) {
                cycle()
                var rect = rect2
                rect.left -= g
                rect = rect2
                rect.right -= h
                rect = rect2
                rect.top -= j
                rect = rect2
                rect.bottom -= i
                if (drawable is LayerDrawable) {
                    val layerDrawable = drawable
                    val k = layerDrawable.numberOfLayers
                    for (i in 0 until k) {
                        if (layerDrawable.getDrawable(i).getPadding(rect)) {
                            layerDrawable.setLayerInset(i,
                                rect2.left - rect1.left,
                                rect2.top - rect1.top,
                                rect2.right - rect1.right,
                                rect2.bottom - rect1.bottom)
                        } else {
                            layerDrawable.setLayerInset(i,
                                rect2.left,
                                rect2.top,
                                rect2.right,
                                rect2.bottom)
                        }
                    }
                }
                drawable.setBounds(x - rect2.left, y - rect2.top, width + rect2.right, height + rect2.bottom)
            } else {
                drawable.setBounds(x, y, width, height)
            }
            drawable.draw(canvas)
        }
    }

    fun setUpDrawable(drawable: Drawable?) {
        if (drawable != null) {
            if (this.drawable == drawable) return
            this.drawable = drawable
            this.view.invalidate()
        }
    }

    override fun setUpDrawable(drawableId: Int) {
        setUpDrawable(ContextCompat.getDrawable(context, drawableId))
    }

    override fun setUpEnabled(bool: Boolean) {
        if (isEnable == bool) return
        isEnable = bool
        this.view.invalidate()
        if (Build.VERSION.SDK_INT < 19) {
            val viewParent = view.parent
            if (viewParent is ViewGroup) {
                viewParent.invalidate()
            }
        }
    }
}