package com.seiko.player.util.bitmap

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem
import java.io.*

class ThumbnailsProvider(private val cacheDir: File) {

    private fun getMediaThumbnailPath(isMedia: Boolean, item: MediaLibraryItem): String? {
        if (isMedia && (item as MediaWrapper).type == MediaWrapper.TYPE_VIDEO && item.artworkMrl.isNullOrEmpty()) {
            if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                return null
            }
            return File(cacheDir, "${item.fileName}.jpg").absolutePath
        }
        return item.artworkMrl
    }

    fun getMediaCacheKey(isMedia: Boolean, item: MediaLibraryItem, width: String = ""): String? {
        return if (width.isEmpty()) {
            getMediaThumbnailPath(isMedia, item)
        } else {
            "${getMediaThumbnailPath(isMedia, item)}_$width"
        }
    }

    @WorkerThread
    suspend fun obtainBitmap(item: MediaLibraryItem, width: Int) = withContext(Dispatchers.IO) {
        when (item) {
            is MediaWrapper -> getMediaThumbnail(item, width)
            else -> BitmapUtils.readCoverBitmap(Uri.decode(item.artworkMrl), width)
        }
    }

    @WorkerThread
    fun getMediaThumbnail(item: MediaWrapper, width: Int): Bitmap? {
        return if (item.type == MediaWrapper.TYPE_VIDEO && TextUtils.isEmpty(item.artworkMrl))
            getVideoThumbnail(item, width)
        else
            BitmapUtils.readCoverBitmap(Uri.decode(item.artworkMrl), width)
    }

    @WorkerThread
    fun getVideoThumbnail(media: MediaWrapper, width: Int): Bitmap? {
        val filePath = media.uri.path ?: return null

        val cacheBM = BitmapCache.getBitmapFromMemCache(getMediaCacheKey(true, media))
        if (cacheBM != null) {
            return cacheBM
        }

        val thumbPath = getMediaThumbnailPath(true, media) ?: return null
        if (File(thumbPath).exists()) {
            return BitmapUtils.readCoverBitmap(thumbPath, width)
        }
        if (media.isThumbnailGenerated) return null
        val bitmap = synchronized(this) {
            ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND)
        }
        if (bitmap != null) {
            BitmapCache.addBitmapToMemCache(thumbPath, bitmap)
            val hasCache = cacheDir.exists() || cacheDir.mkdirs()
            if (hasCache) {
                media.setThumbnail(thumbPath)
                saveOnDisk(bitmap, thumbPath)
                media.artworkURL = thumbPath
            }
        } else if (media.id != 0L) {
            media.requestThumbnail(width, 0.4f)
        }
        return bitmap
    }
}

private fun saveOnDisk(bitmap: Bitmap, destPath: String) {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    val byteArray = stream.toByteArray()
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(destPath)
        fos.write(byteArray)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        close(fos)
        close(stream)
    }
}

private fun close(closeable: Closeable?) {
    if (closeable != null) {
        try {
            closeable.close()
        } catch (e: IOException) {
        }
    }
}