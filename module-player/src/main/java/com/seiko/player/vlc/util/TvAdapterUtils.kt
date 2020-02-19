package com.seiko.player.vlc.util

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.os.Build
import androidx.core.content.ContextCompat
import com.seiko.player.R
import com.seiko.player.ui.widget.FocusableConstraintLayout

object TvAdapterUtils {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun itemFocusChange(hasFocus: Boolean, itemSize: Int, container: FocusableConstraintLayout, isList: Boolean, listener: () -> Unit) {
        if (hasFocus) {
            val growFactor = if (isList) 1.05 else 1.1
            var newWidth = (itemSize * growFactor).toInt()
            if (newWidth % 2 == 1) {
                newWidth--
            }
            val scale = newWidth.toFloat() / itemSize
            container.animate().scaleX(scale).scaleY(scale).translationZ(scale)

            listener()
        } else {
            container.animate().scaleX(1f).scaleY(1f).translationZ(1f)
        }

        if (isList) {
            val colorFrom = ContextCompat.getColor(container.context, R.color.tv_card_content_dark)
            val colorTo = ContextCompat.getColor(container.context, R.color.tv_card_content)

            val colorAnimation = if (hasFocus) ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo) else ValueAnimator.ofObject(
                ArgbEvaluator(), colorTo, colorFrom)
            colorAnimation.duration = 250 // milliseconds

            colorAnimation.addUpdateListener { animator -> container.setBackgroundColor(animator.animatedValue as Int) }
            colorAnimation.start()
        }
    }

}