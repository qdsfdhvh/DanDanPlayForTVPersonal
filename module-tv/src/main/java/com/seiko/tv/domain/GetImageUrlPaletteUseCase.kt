package com.seiko.tv.domain

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.URLUtil
import androidx.palette.graphics.Palette
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequestBuilder
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 提取图片中的颜色
 * Fresco下载图片，Palette提取颜色
 */
class GetImageUrlPaletteUseCase : KoinComponent {

    suspend operator fun invoke(imageUrl: String): Palette? {
        if (!URLUtil.isNetworkUrl(imageUrl)) return null

        return suspendCoroutine { continuation ->
            val uri = Uri.parse(imageUrl)
            val imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).build()
            val imagePipeline = Fresco.getImagePipeline()
            val context: Context by inject()
            val dataSource = imagePipeline.fetchDecodedImage(imageRequest, context)
            val dataSubscriber = object : BaseBitmapDataSubscriber() {
                override fun onNewResultImpl(bitmap: Bitmap?) {
                    if (bitmap == null) {
                        continuation.resume(null)
                        return
                    }
                    Palette.Builder(bitmap).clearFilters().generate { palette ->
                        continuation.resume(palette)
                    }
                }

                override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>?) {
                    continuation.resume(null)
                }
            }
            dataSource.subscribe(dataSubscriber, CallerThreadExecutor.getInstance())
        }
    }

}