package com.seiko.player.util

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import androidx.core.animation.addListener

object AnimatorUtils {

    fun translationXShow(view: View, startX: Float = -800f, endX: Float = 0f, duration: Long = 800,
                         onStart: (animator: Animator) -> Unit = {}) {
        val translationX = ObjectAnimator.ofFloat(view, "translationX", startX, endX)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        val set = AnimatorSet()
        set.duration = duration
        set.playTogether(translationX, alpha)
        set.addListener(onStart = onStart)
        set.start()
    }

    fun translationXHide(view: View, startX: Float = 0f, endX: Float = -800f, duration: Long = 800,
                         onEnd: (animator: Animator) -> Unit = {}) {
        val translationX = ObjectAnimator.ofFloat(view, "translationX", startX, endX)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        val set = AnimatorSet()
        set.duration = duration
        set.playTogether(translationX, alpha)
        set.addListener(onEnd = onEnd)
        set.start()
    }
}