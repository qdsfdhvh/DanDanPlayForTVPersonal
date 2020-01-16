package com.dandanplay.tv.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.URLUtil
import androidx.palette.graphics.Palette
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeController
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor
import com.facebook.imagepipeline.request.ImageRequestBuilder

fun getFrescoBlurBitmap(context: Context, imageUrl: String, callback: (Bitmap) -> Unit) {
    if (!URLUtil.isNetworkUrl(imageUrl)) return

    val uri = Uri.parse(imageUrl)
    val imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
        .setPostprocessor(IterativeBoxBlurPostProcessor(1, 2))
        .build()
    val imagePipeline = Fresco.getImagePipeline()
    val dataSource = imagePipeline.fetchDecodedImage(imageRequest, context)
    val dataSubscriber = object : BaseBitmapDataSubscriber() {
        override fun onNewResultImpl(bitmap: Bitmap?) {
            bitmap?.let(callback)
        }

        override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>?) {

        }
    }
    dataSource.subscribe(dataSubscriber, CallerThreadExecutor.getInstance())
}