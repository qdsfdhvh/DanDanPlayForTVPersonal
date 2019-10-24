package com.dandanplay.tv2.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView

class CircleImageView: SimpleDraweeView {

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val hierarchy = hierarchy
        if (isInEditMode) {
            hierarchy.roundingParams = null
            return
        }
        if (hierarchy.roundingParams == null) {
            val param = RoundingParams()
            param.roundAsCircle = true
            param.roundingMethod = RoundingParams.RoundingMethod.BITMAP_ONLY
            hierarchy.roundingParams = param
        }
    }

    fun setBorder(@ColorInt color: Int, width: Float) {
        val hierarchy = hierarchy
        if (isInEditMode) {
            hierarchy.roundingParams = null
            return
        }
        val param = RoundingParams()
        param.roundAsCircle = true
        param.setBorder(color, width)
        param.roundingMethod = RoundingParams.RoundingMethod.BITMAP_ONLY
        hierarchy.roundingParams = param
    }


}