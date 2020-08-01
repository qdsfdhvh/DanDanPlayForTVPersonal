package com.seiko.tv.domain

import android.app.Application
import android.content.Context
import android.webkit.URLUtil
import androidx.palette.graphics.Palette
import com.seiko.common.util.getBitmap
import org.koin.core.KoinComponent
import javax.inject.Inject

/**
 * 提取图片中的颜色
 * Fresco下载图片，Palette提取颜色
 */
class GetImageUrlPaletteUseCase @Inject constructor(
    private val app: Application
) : KoinComponent {
    suspend operator fun invoke(imageUrl: String): Palette? {
        if (!URLUtil.isNetworkUrl(imageUrl)) return null

        val bitmap = app.getBitmap(imageUrl)
        return Palette.Builder(bitmap).generate()
    }
}