package com.seiko.common.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.seiko.common.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ImageLoaderImpl @Inject constructor(
    @ApplicationContext context: Context
) : ImageLoader {

    private val glide by lazy(LazyThreadSafetyMode.NONE) {
        TvGlide.with(context)
    }

    override fun loadGridImage(view: ImageView, url: String) {
        glide.load(url)
            .placeholder(R.drawable.picture_icon_placeholder)
            .override(160, 200)
            .centerCrop()
            .into(view)
    }

    override fun loadGridImage(view: ImageView, resId: Int) {
        glide.load(resId)
            .placeholder(R.drawable.picture_icon_placeholder)
            .override(80, 80)
            .centerInside()
            .into(view)
    }

    override fun loadImage(view: ImageView, url: String) {
        glide.load(url)
            .placeholder(R.drawable.picture_icon_placeholder)
            .centerCrop()
            .into(view)
    }

    override fun loadImage(view: ImageView, resId: Int) {
        glide.load(resId)
            .placeholder(R.drawable.picture_icon_placeholder)
            .centerCrop()
            .into(view)
    }

    override suspend fun getDrawable(url: String): Drawable? {
        return glide.getDrawable(url)
    }

    override suspend fun getBitMap(url: String): Bitmap? {
        return glide.getBitmap(url)
    }
}

private suspend fun GlideRequests.getDrawable(url: String): Drawable {
    return suspendCoroutine { continuation ->
        asDrawable().load(url).into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                continuation.resume(resource)
            }
            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
    }
}

private suspend fun GlideRequests.getBitmap(url: String): Bitmap {
    return suspendCoroutine { continuation ->
        asBitmap().load(url).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                continuation.resume(resource)
            }
            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
    }
}