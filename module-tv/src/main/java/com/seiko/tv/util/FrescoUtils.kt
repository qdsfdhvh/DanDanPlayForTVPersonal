package com.seiko.tv.util

import android.app.Activity
import android.app.Application
import android.app.SharedElementCallback
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.View
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.util.ByteConstants
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.DraweeTransition
import com.facebook.drawee.view.GenericDraweeView
import com.facebook.imagepipeline.cache.MemoryCacheParams
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.core.ImagePipeline
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig
import com.facebook.imagepipeline.request.ImageRequestBuilder
import java.io.File

private val MAX_HEAP_SIZE = Runtime.getRuntime().maxMemory().toInt()
private val MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4
private const val MAX_DISK_CACHE_SIZE = 50L * ByteConstants.MB

fun Application.initFresco() {
    val diskCacheConfig = DiskCacheConfig.newBuilder(this@initFresco)
        .setBaseDirectoryPath(cacheDir)
        .setBaseDirectoryName(packageName)
        .setMaxCacheSize(MAX_DISK_CACHE_SIZE) // max cache size
        .build()

    val config = ImagePipelineConfig.newBuilder(this@initFresco)
        .setBitmapMemoryCacheParamsSupplier {
            MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE,
                Int.MAX_VALUE,
                MAX_MEMORY_CACHE_SIZE,
                Int.MAX_VALUE,
                Int.MAX_VALUE)
        }
        .setMainDiskCacheConfig(diskCacheConfig)
        .setDownsampleEnabled(true)
//        .setProgressiveJpegConfig(SimpleProgressiveJpegConfig())
//        .setResizeAndRotateEnabledForNetwork(true)
        .setBitmapsConfig(Bitmap.Config.RGB_565)
        .build()

    Fresco.initialize(this@initFresco, config)
}

fun getImagePipeline(): ImagePipeline {
    return ImagePipelineFactory.getInstance().imagePipeline
}

fun clearFrescoMemory() {
    ImagePipelineFactory.getInstance().imagePipeline.clearMemoryCaches()
}

fun GenericDraweeView.loadImage(url: String?) {
    if (url.isNullOrEmpty()) return
    val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
        .setResizeOptions(ResizeOptions.forSquareSize(240))
        .setProgressiveRenderingEnabled(false)
        .build()

    controller = Fresco.newDraweeControllerBuilder()
        .setImageRequest(request)
        .setOldController(controller)
        .setAutoPlayAnimations(true)
        .build()
}

//fun GenericDraweeView.loadFileImage(path: String) {
//    val uri = Uri.fromFile(File(path))
//    val request =ImageRequestBuilder.newBuilderWithSource(uri)
//        .build()
//    controller = Fresco.newDraweeControllerBuilder()
//        .setImageRequest(request)
//        .setOldController(controller)
//        .setAutoPlayAnimations(true)
//        .build()
//}
//
//fun GenericDraweeView.loadImage(url: String, resizeOptions: ResizeOptions) {
//    val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
//        .setResizeOptions(resizeOptions)
//        .setProgressiveRenderingEnabled(false)
//        .build()
//
//    controller = Fresco.newDraweeControllerBuilder()
//        .setImageRequest(request)
//        .setOldController(controller)
//        .setAutoPlayAnimations(true)
//        .build()
//}
