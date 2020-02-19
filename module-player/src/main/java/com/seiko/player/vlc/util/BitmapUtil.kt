package com.seiko.player.vlc.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.seiko.player.R
import org.videolan.medialibrary.interfaces.media.MediaWrapper

object BitmapUtil {
    const val TAG = "VLC/UiTools/BitmapUtil"


//    fun getPictureFromCache(media: MediaWrapper): Bitmap? {
//        // mPicture is not null only if passed through
//        // the ctor which is deprecated by now.
//        val b = media.picture
//        return b ?: BitmapCache.getBitmapFromMemCache(media.location)
//    }
//
//    private fun fetchPicture(media: MediaWrapper): Bitmap? {
//        val picture = readCoverBitmap(media.artworkURL)
//        if (picture != null) BitmapCache.addBitmapToMemCache(media.location, picture)
//        return picture
//    }
//
//    fun getPicture(media: MediaWrapper): Bitmap? {
//        val picture = getPictureFromCache(media)
//        return picture ?: fetchPicture(media)
//    }
//
//    private fun readCoverBitmap(path: String?): Bitmap? {
//        if (path == null) return null
//        val ctx = AppContextProvider.appContext
//        val res = ctx.resources
//        var uri = Uri.decode(path)
//        if (uri.startsWith("file://")) uri = uri.substring(7)
//        var cover: Bitmap? = null
//        val options = BitmapFactory.Options()
//        val height = res.getDimensionPixelSize(R.dimen.grid_card_thumb_height)
//        val width = res.getDimensionPixelSize(R.dimen.grid_card_thumb_width)
//
//        /* Get the resolution of the bitmap without allocating the memory */
//        options.inJustDecodeBounds = true
//        BitmapFactory.decodeFile(uri, options)
//
//        if (options.outWidth > 0 && options.outHeight > 0) {
//            if (options.outWidth > width) {
//                options.outWidth = width
//                options.outHeight = height
//            }
//            options.inJustDecodeBounds = false
//
//            // Decode the file (with memory allocation this time)
//            try {
//                cover = BitmapFactory.decodeFile(uri, options)
//            } catch (e: OutOfMemoryError) {
//                cover = null
//            }
//
//        }
//
//        return cover
//    }


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