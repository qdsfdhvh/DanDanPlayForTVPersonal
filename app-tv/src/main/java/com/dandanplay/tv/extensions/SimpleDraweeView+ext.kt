package com.dandanplay.tv.extensions

import android.net.Uri
import androidx.annotation.DrawableRes
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.facebook.common.util.UriUtil


fun SimpleDraweeView.showUrlBlur(url: String, iterations: Int, blurRadius: Int) {
    try {
        val uri = Uri.parse(url)
        val request = ImageRequestBuilder.newBuilderWithSource(uri)
            .setPostprocessor(IterativeBoxBlurPostProcessor(iterations, blurRadius))
            .build()
        val controller = Fresco.newDraweeControllerBuilder()
            .setOldController(this.controller)
            .setImageRequest(request)
            .build()
        setController(controller)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun SimpleDraweeView.setDrawableRes(@DrawableRes drawableId: Int) {
    val uri = Uri.Builder()
        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
        .path(java.lang.String.valueOf(drawableId))
        .build()
    val controller = Fresco.newDraweeControllerBuilder()
        .setUri(uri)
        .setOldController(this.controller)
        .build()
    setController(controller)
}