package com.seiko.player.util.bitmap

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.text.TextUtils
import android.widget.ImageView
import androidx.leanback.app.BackgroundManager
import com.seiko.common.util.extensions.getScreenHeight
import com.seiko.common.util.extensions.getScreenWidth
import com.seiko.player.R
import kotlinx.coroutines.*
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem

class ImageLoader(private val provider: ThumbnailsProvider) {

    private var defaultImageWidthTV = 0

    suspend fun loadImage(imageView: ImageView, item: MediaLibraryItem) {
        val isMedia = item.itemType == MediaLibraryItem.TYPE_MEDIA
        val cacheKey = provider.getMediaCacheKey(isMedia, item)
        val bitmap = if (cacheKey != null) BitmapCache.getBitmapFromMemCache(cacheKey) else null
        if (bitmap != null) {
            updateImageView(bitmap, imageView)
        } else {
            withContext(Dispatchers.IO) {
                getImage(imageView,
                    findInLibrary(item, isMedia)
                )
            }
        }
    }

    private suspend fun getImage(view: ImageView, item: MediaLibraryItem) {
        if (defaultImageWidthTV == 0) {
            defaultImageWidthTV = view.resources.getDimensionPixelSize(R.dimen.tv_grid_card_thumb_width)
        }
        val image = provider.obtainBitmap(item, defaultImageWidthTV)
        if (image != null) {
            updateImageView(image, view)
        }
    }

    private suspend fun updateImageView(bitmap: Bitmap, target: ImageView) {
        if (bitmap.width<= 1 || bitmap.height <= 1) return
        withContext(Dispatchers.Main) {
            target.scaleType = ImageView.ScaleType.CENTER_CROP
            target.setImageBitmap(bitmap)
        }
    }

    suspend fun updateBackground(activity: Activity, bm: BackgroundManager?, item: MediaLibraryItem?) {
        if (bm === null || item === null) {
            clearBackground(bm)
            return
        }
        val screenRatio: Float = activity.getScreenWidth().toFloat() / activity.getScreenHeight()
        withContext(Dispatchers.IO) {
            val artworkMrl = item.artworkMrl
            if (!TextUtils.isEmpty(artworkMrl)) {
                var cover: Bitmap? = BitmapUtils.readCoverBitmap(Uri.decode(artworkMrl), 512) ?: return@withContext
                if (cover != null) {
                    cover = BitmapUtils.centerCrop(cover, cover.width, (cover.width / screenRatio).toInt())
                }
                val blurred = BitmapUtils.blurBitmap(activity, cover, 10f)
                if (!isActive) return@withContext
                bm.color = 0
                bm.drawable = BitmapDrawable(activity.resources, blurred)
            }
        }
    }

    fun clearBackground(bm: BackgroundManager?) {
        bm?.drawable = null
    }
}

private suspend fun findInLibrary(item: MediaLibraryItem, isMedia: Boolean): MediaLibraryItem {
    if (isMedia && item.id == 0L) {
        val mw = item as MediaWrapper
        val type = mw.type
        val isMediaFile = type == MediaWrapper.TYPE_AUDIO || type == MediaWrapper.TYPE_VIDEO
        val uri = mw.uri
        if (!isMediaFile && !(type == MediaWrapper.TYPE_DIR && "upnp" == uri.scheme)) return item
        if (isMediaFile && "file" == uri.scheme) return withContext(Dispatchers.IO) {
            Medialibrary.getInstance().getMedia(uri)
        } ?: item
    }
    return item
}