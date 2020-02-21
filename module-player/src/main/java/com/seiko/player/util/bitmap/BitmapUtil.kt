package com.seiko.player.util.bitmap

import android.graphics.Bitmap

object BitmapUtil {

    /**
     * 裁剪图片
     */
    fun centerCrop(srcBmp: Bitmap, width: Int, height: Int): Bitmap {
        val widthDiff = srcBmp.width - width
        val heightDiff = srcBmp.height - height
        if (widthDiff <= 0 && heightDiff <= 0) return srcBmp
        return try {
            Bitmap.createBitmap(
                srcBmp,
                widthDiff / 2,
                heightDiff / 2,
                width,
                height
            )
        } catch (ignored: Exception) {
            srcBmp
        }
    }
}