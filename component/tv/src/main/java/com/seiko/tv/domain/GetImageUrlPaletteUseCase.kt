package com.seiko.tv.domain

import android.content.Context
import android.webkit.URLUtil
import androidx.palette.graphics.Palette
import com.seiko.common.util.getBitmap
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 提取图片中的颜色
 * Fresco下载图片，Palette提取颜色
 */
class GetImageUrlPaletteUseCase : KoinComponent {

    private val context: Context by inject()

    suspend operator fun invoke(imageUrl: String): Palette? {
        if (!URLUtil.isNetworkUrl(imageUrl)) return null

        val bitmap = context.getBitmap(imageUrl)
        return Palette.Builder(bitmap).generate()
    }

}