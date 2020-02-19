package com.seiko.player.vlc.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.*
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.Folder
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.interfaces.media.VideoGroup
import kotlin.math.min

const val MEDIA_LIBRARY_PAGE_SIZE = 500

@WorkerThread
fun Folder.getAll(type: Int = Folder.TYPE_FOLDER_VIDEO, sort: Int = Medialibrary.SORT_DEFAULT, desc: Boolean = false): List<MediaWrapper> {
    var index = 0
    val count = mediaCount(type)
    val all = mutableListOf<MediaWrapper>()
    while (index < count) {
        val pageCount = min(MEDIA_LIBRARY_PAGE_SIZE, count - index)
        val list = media(type, sort, desc, pageCount, index)
        all.addAll(list)
        index += pageCount
    }
    return all
}

@WorkerThread
fun VideoGroup.getAll(sort: Int = Medialibrary.SORT_DEFAULT, desc: Boolean = false): List<MediaWrapper> {
    var index = 0
    val count = mediaCount()
    val all = mutableListOf<MediaWrapper>()
    while (index < count) {
        val pageCount = min(MEDIA_LIBRARY_PAGE_SIZE, count - index)
        val list = media(sort, desc, pageCount, index)
        all.addAll(list)
        index += pageCount
    }
    return all
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