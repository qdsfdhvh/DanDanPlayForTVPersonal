package com.seiko.tv.domain

import android.webkit.URLUtil
import androidx.palette.graphics.Palette
import com.seiko.common.imageloader.ImageLoader
import javax.inject.Inject

/**
 * 提取图片中的颜色
 */
class GetImageUrlPaletteUseCase @Inject constructor(
    private val imageLoader: ImageLoader
) {
    suspend operator fun invoke(imageUrl: String): Palette? {
        if (!URLUtil.isNetworkUrl(imageUrl)) return null
        val bitmap = imageLoader.getBitmap(imageUrl) ?: return null
        return Palette.Builder(bitmap).generate()
    }
}