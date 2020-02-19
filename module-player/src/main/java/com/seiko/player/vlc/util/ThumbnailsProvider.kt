package com.seiko.player.vlc.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.Folder
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.interfaces.media.VideoGroup
import org.videolan.medialibrary.media.MediaLibraryItem
import java.io.*
import kotlin.math.min

class ThumbnailsProvider(private val context: Context) {

    companion object {
        private const val MAX_IMAGES = 4
    }

    private var appDir: File? = null
    private var cacheDir: String? = null

    private fun getMediaThumbnailPath(isMedia: Boolean, item: MediaLibraryItem): String? {
        if (isMedia && (item as MediaWrapper).type == MediaWrapper.TYPE_VIDEO && TextUtils.isEmpty(item.getArtworkMrl())) {
            if (appDir == null) appDir = context.getExternalFilesDir(null)
            val hasCache = appDir != null && appDir!!.exists()
            if (hasCache && cacheDir == null) cacheDir = appDir!!.absolutePath + Medialibrary.MEDIALIB_FOLDER_NAME
            return if (hasCache) StringBuilder(cacheDir!!).append('/').append(item.fileName).append(".jpg").toString() else null
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

    suspend fun obtainBitmap(item: MediaLibraryItem, width: Int) = withContext(Dispatchers.IO) {
        when (item) {
            is MediaWrapper -> getMediaThumbnail(item, width)
            is Folder -> getFolderThumbnail(item, width)
            is VideoGroup -> getVideoGroupThumbnail(item, width)
            else -> readCoverBitmap(Uri.decode(item.artworkMrl), width)
        }
    }

    @WorkerThread
    fun getMediaThumbnail(item: MediaWrapper, width: Int): Bitmap? {
        return if (item.type == MediaWrapper.TYPE_VIDEO && TextUtils.isEmpty(item.artworkMrl))
            getVideoThumbnail(item, width)
        else
            readCoverBitmap(Uri.decode(item.artworkMrl), width)
    }

    @WorkerThread
    fun getVideoThumbnail(media: MediaWrapper, width: Int): Bitmap? {
        val filePath = media.uri.path ?: return null
        if (appDir == null) appDir = context.getExternalFilesDir(null)
        val hasCache = appDir?.exists() == true
        val thumbPath = getMediaThumbnailPath(true, media) ?: return null
        val cacheBM = if (hasCache) BitmapCache.getBitmapFromMemCache(getMediaCacheKey(true, media)) else null
        if (cacheBM != null) return cacheBM
        if (hasCache && File(thumbPath).exists()) return readCoverBitmap(thumbPath, width)
        if (media.isThumbnailGenerated) return null
        val bitmap = synchronized(this) {
            ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND)
        }
        if (bitmap != null) {
            BitmapCache.addBitmapToMemCache(thumbPath, bitmap)
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

    @WorkerThread
    fun getFolderThumbnail(folder: Folder, width: Int): Bitmap? {
        val media = folder.media(Folder.TYPE_FOLDER_VIDEO, Medialibrary.SORT_DEFAULT, true, 4, 0).filterNotNull()
        return getComposedImage("folder:${folder.title}", media, width)
    }

    @WorkerThread
    fun getVideoGroupThumbnail(group: VideoGroup, width: Int): Bitmap? {
        val media = group.media(Medialibrary.SORT_DEFAULT, true, 4, 0).filterNotNull()
        return getComposedImage("videogroup:${group.title}", media, width)
    }

    @WorkerThread
    fun getComposedImage(key: String, mediaList: List<MediaWrapper>, width: Int): Bitmap? {
        var composedImage = BitmapCache.getBitmapFromMemCache(key)
        if (composedImage == null) {
            composedImage = composeImage(mediaList, width)
            if (composedImage != null) BitmapCache.addBitmapToMemCache(key, composedImage)
        }
        return composedImage
    }

    /**
     * Compose 1 image from combined media thumbnails
     * @param mediaList The media list from which will extract thumbnails
     * @return a Bitmap object
     */
    private fun composeImage(mediaList: List<MediaWrapper>, imageWidth: Int): Bitmap? {
        val sourcesImages = arrayOfNulls<Bitmap>(min(MAX_IMAGES, mediaList.size))
        var count = 0
        var minWidth = Integer.MAX_VALUE
        var minHeight = Integer.MAX_VALUE
        for (media in mediaList) {
            val bm = getVideoThumbnail(media, imageWidth)
            if (bm != null) {
                val width = bm.width
                val height = bm.height
                sourcesImages[count++] = bm
                minWidth = min(minWidth, width)
                minHeight = min(minHeight, height)
                if (count == MAX_IMAGES) break
            }
        }
        if (count == 0) return null

        return if (count == 1) sourcesImages[0] else composeCanvas(sourcesImages.filterNotNull().toTypedArray(), count, minWidth, minHeight)
    }

    private fun composeCanvas(sourcesImages: Array<Bitmap>, count: Int, minWidth: Int, minHeight: Int): Bitmap {
        val overlayWidth: Int
        val overlayHeight: Int
        when (count) {
            4 -> {
                overlayWidth = 2 * minWidth
                overlayHeight = 2 * minHeight
            }
            else -> {
                overlayWidth = minWidth
                overlayHeight = minHeight
            }
        }
        val bmOverlay = Bitmap.createBitmap(overlayWidth, overlayHeight, sourcesImages[0].config)

        val canvas = Canvas(bmOverlay)
        when (count) {
            2 -> {
                for (i in 0 until count)
                    sourcesImages[i] = BitmapUtil.centerCrop(sourcesImages[i], minWidth / 2, minHeight)
                canvas.drawBitmap(sourcesImages[0], 0f, 0f, null)
                canvas.drawBitmap(sourcesImages[1], (minWidth / 2).toFloat(), 0f, null)
            }
            3 -> {
                sourcesImages[0] = BitmapUtil.centerCrop(sourcesImages[0], minWidth / 2, minHeight / 2)
                sourcesImages[1] = BitmapUtil.centerCrop(sourcesImages[1], minWidth / 2, minHeight / 2)
                sourcesImages[2] = BitmapUtil.centerCrop(sourcesImages[2], minWidth, minHeight / 2)
                canvas.drawBitmap(sourcesImages[0], 0f, 0f, null)
                canvas.drawBitmap(sourcesImages[1], (minWidth / 2).toFloat(), 0f, null)
                canvas.drawBitmap(sourcesImages[2], 0f, (minHeight / 2).toFloat(), null)
            }
            4 -> {
                for (i in 0 until count)
                    sourcesImages[i] = BitmapUtil.centerCrop(sourcesImages[i], minWidth, minHeight)
                canvas.drawBitmap(sourcesImages[0], 0f, 0f, null)
                canvas.drawBitmap(sourcesImages[1], minWidth.toFloat(), 0f, null)
                canvas.drawBitmap(sourcesImages[2], 0f, minHeight.toFloat(), null)
                canvas.drawBitmap(sourcesImages[3], minWidth.toFloat(), minHeight.toFloat(), null)
            }
        }
        return bmOverlay
    }

    @WorkerThread
    fun readCoverBitmap(path: String?, width: Int): Bitmap? {
        var path: String? = path ?: return null
//        if (path!!.startsWith("http")) return HttpImageLoader.downloadBitmap(path)
        if (path!!.startsWith("file")) path = path.substring(7)
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
}

private fun close(closeable: Closeable?): Boolean {
    if (closeable != null)
        try {
            closeable.close()
            return true
        } catch (e: IOException) {
        }

    return false
}