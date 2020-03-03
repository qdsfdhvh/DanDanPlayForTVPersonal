package com.seiko.player.util.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.renderscript.*
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread

object BitmapUtils {

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


    @WorkerThread
    fun readCoverBitmap(filePath: String?, width: Int): Bitmap? {
        if (filePath == null) return null
//        if (path!!.startsWith("http")) return HttpImageLoader.downloadBitmap(path)
        val path = if (filePath.startsWith("file")) {
            filePath.substring(7)
        } else filePath
        var cover: Bitmap? = null
        val options = BitmapFactory.Options()

        /* Get the resolution of the bitmap without allocating the memory */
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        if (options.outWidth > 0 && options.outHeight > 0) {
            options.inJustDecodeBounds = false
            options.inSampleSize = 1

            // Find the best decoding scale for the bitmap
            if (width > 0) {
                while (options.outWidth / options.inSampleSize > width)
                    options.inSampleSize = options.inSampleSize * 2
            }

            // Decode the file (with memory allocation this time)
            cover = BitmapFactory.decodeFile(path, options)
            BitmapCache.addBitmapToMemCache(path, cover)
        }
        return cover
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @JvmOverloads
    fun blurBitmap(context: Context, bitmap: Bitmap?, radius: Float = 15.0f): Bitmap? {
        if (bitmap == null || bitmap.config == null) return null
        try {
            //Let's create an empty bitmap with the same size of the bitmap we want to blur
            val outBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

            //Instantiate a new Renderscript
            val rs = RenderScript.create(context)

            //Create an Intrinsic Blur Script using the Renderscript
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))


            //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
            val allIn = Allocation.createFromBitmap(rs, bitmap)
            val allOut = Allocation.createFromBitmap(rs, outBitmap)

            //Set the radius of the blur
            blurScript.setRadius(radius)

            //Perform the Renderscript
            blurScript.setInput(allIn)
            blurScript.forEach(allOut)

            //Copy the final bitmap created by the out Allocation to the outBitmap
            allOut.copyTo(outBitmap)

            //After finishing everything, we destroy the Renderscript.
            rs.destroy()

            return outBitmap
        } catch (ignored: RSInvalidStateException) {
            return null
        }
    }
}