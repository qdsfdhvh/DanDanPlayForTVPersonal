package com.seiko.tv.util

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.util.ByteConstants
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.GenericDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.core.ImagePipeline
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.request.ImageRequestBuilder

private const val MAX_DISK_CACHE_SIZE = 100L * ByteConstants.MB

fun Application.initFresco() {
    val diskCacheConfig = DiskCacheConfig.newBuilder(this@initFresco)
        .setBaseDirectoryPath(cacheDir)
        .setBaseDirectoryName("stuff")
        .setMaxCacheSize(MAX_DISK_CACHE_SIZE) // max cache size
        .build()

    val config = ImagePipelineConfig.newBuilder(this@initFresco)
        .setMainDiskCacheConfig(diskCacheConfig)
        .setBitmapsConfig(Bitmap.Config.RGB_565)
        .setDownsampleEnabled(true)
        .build()

    Fresco.initialize(this@initFresco, config)
}

fun getImagePipeline(): ImagePipeline {
    return ImagePipelineFactory.getInstance().imagePipeline
}

fun clearFrescoMemory() {
    getImagePipeline().clearMemoryCaches()
}

private val resizeOptions by lazy(LazyThreadSafetyMode.NONE) {
    ResizeOptions.forSquareSize(240)
}

fun GenericDraweeView.loadImage(url: String?) {
    if (url.isNullOrEmpty()) return
    val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
        .setResizeOptions(resizeOptions)
        .setProgressiveRenderingEnabled(false)
        .build()

    controller = Fresco.newDraweeControllerBuilder()
        .setImageRequest(request)
        .setOldController(controller)
        .setAutoPlayAnimations(true)
        .build()
}