package com.seiko.player.vlc.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BackgroundManager
import androidx.lifecycle.lifecycleScope
import com.seiko.common.util.extensions.getScreenHeight
import com.seiko.common.util.extensions.getScreenWidth
import com.seiko.player.R
import kotlinx.coroutines.*
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem

class ImageLoader(
    private val activity: Activity,
    private val provider: ThumbnailsProvider,
    private val scope: CoroutineScope
) {

    constructor(activity: FragmentActivity) : this(activity, ThumbnailsProvider(activity.applicationContext), activity.lifecycleScope)

    private var defaultImageWidthTV = 0

    fun loadImage(imageView: ImageView, item: MediaLibraryItem) {
        val isMedia = item.itemType == MediaLibraryItem.TYPE_MEDIA
        val cacheKey = provider.getMediaCacheKey(isMedia, item)
        val bitmap = if (cacheKey != null) BitmapCache.getBitmapFromMemCache(cacheKey) else null
        if (bitmap != null) {
            updateImageView(bitmap, imageView)
        } else {
            scope.takeIf { it.isActive }?.launch {
                getImage(imageView, findInLibrary(item, isMedia))
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

    @MainThread
    fun updateImageView(bitmap: Bitmap, target: ImageView) {
        if (bitmap.width<= 1 || bitmap.height <= 1) return
        target.scaleType = ImageView.ScaleType.CENTER_CROP
        target.setImageBitmap(bitmap)
    }

    fun updateBackground(bm: BackgroundManager?, item: MediaLibraryItem?) {
        if (bm === null || item === null) {
            clearBackground(bm)
            return
        }
        val screenRatio: Float = activity.getScreenWidth().toFloat() / activity.getScreenHeight()
        scope.launch {
            val artworkMrl = item.artworkMrl
            if (!TextUtils.isEmpty(artworkMrl)) {
                val blurred = withContext(Dispatchers.IO) {
                    var cover: Bitmap? = provider.readCoverBitmap(Uri.decode(artworkMrl), 512)
                        ?: return@withContext null
                    if (cover != null) cover = BitmapUtil.centerCrop(cover, cover.width, (cover.width / screenRatio).toInt())
                    blurBitmap(activity, cover, 10f)
                }
                if (!isActive) return@launch
                bm.color = 0
                bm.drawable = BitmapDrawable(activity.resources, blurred)
            }
//            else if (item.itemType == MediaLibraryItem.TYPE_PLAYLIST) {
//                val blurred = withContext(Dispatchers.IO) {
//                    var cover: Bitmap? = provider.getPlaylistImage("playlist:${item.id}", item.tracks.toList(), 512)
//                        ?: return@withContext null
//                    cover = cover?.let { BitmapUtil.centerCrop(it, it.width, (it.width / screenRatio).toInt()) }
//                    UiTools.blurBitmap(cover, 10f)
//                }
//                if (!isActive) return@launch
//                bm.color = 0
//                bm.drawable = BitmapDrawable(activity.resources, blurred)
//            }
        }
    }

    fun clearBackground(bm: BackgroundManager?) {
        if (bm === null) return
        bm.color = ContextCompat.getColor(activity, R.color.tv_bg)
        bm.drawable = null
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