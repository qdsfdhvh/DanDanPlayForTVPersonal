package com.seiko.tv.util.helper

import android.animation.ObjectAnimator
import android.view.View
import com.seiko.common.util.extensions.lazyAndroid

/**
 * 给CardView Image 添加动画
 */
class CardFadeInDelegate(private val imageView: View) {

    init {
        imageView.visibility = View.INVISIBLE
    }

    private var attachedToWindow = false

    private val fadeInAnimator by lazyAndroid {
        ObjectAnimator.ofFloat(imageView, "alpha", 1f)
            .setDuration(imageView.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
    }

    fun startAnim(hasImage: Boolean, fade: Boolean = true) {
        if (!hasImage) {
            fadeInAnimator.cancel()
            imageView.alpha = 1f
            imageView.visibility = View.INVISIBLE
        } else {
            imageView.visibility = View.VISIBLE
            if (fade) {
                fadeIn()
            } else {
                fadeInAnimator.cancel()
                imageView.alpha = 1f
            }
        }
    }

    private fun fadeIn() {
        imageView.alpha = 0f
        if (attachedToWindow) {
            fadeInAnimator.start()
        }
    }

    fun onAttachedToWindow() {
        attachedToWindow = true
        if (imageView.alpha == 0f) {
            fadeIn()
        }
    }

    fun onDetachedFromWindow() {
        attachedToWindow = false
        fadeInAnimator.cancel()
        imageView.alpha = 1f
    }

}