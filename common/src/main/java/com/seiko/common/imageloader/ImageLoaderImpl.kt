package com.seiko.common.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import coil.load
import coil.request.ImageRequest
import com.seiko.common.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImageLoaderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageLoader {

    private val imageLoader = coil.ImageLoader(context)

    override fun loadGridImage(view: ImageView, url: String) {
        view.load(url, imageLoader) {
            placeholder(R.drawable.picture_icon_placeholder)
            size(160, 200)
        }
    }

    override fun loadGridImage(view: ImageView, resId: Int) {
        view.load(resId, imageLoader) {
            placeholder(R.drawable.picture_icon_placeholder)
            size(80, 80)
        }
    }

    override fun loadImage(view: ImageView, url: String) {
        view.load(url, imageLoader) {
            placeholder(R.drawable.picture_icon_placeholder)
        }
    }

    override fun loadImage(view: ImageView, resId: Int) {
        view.load(resId, imageLoader) {
            placeholder(R.drawable.picture_icon_placeholder)
        }
    }

    override suspend fun getDrawable(url: String): Drawable? {
        val request = ImageRequest.Builder(context)
            .data(url)
            .build()
        return imageLoader.execute(request).drawable
    }

    override suspend fun getBitmap(url: String): Bitmap? {
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build()
        return (imageLoader.execute(request).drawable as? BitmapDrawable)?.bitmap
    }
}