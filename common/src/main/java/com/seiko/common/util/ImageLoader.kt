package com.seiko.common.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.seiko.common.R
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun ImageView.loadGridImage(url: String) {
    TvGlide.with(context).load(url)
        .placeholder(R.drawable.picture_icon_placeholder)
        .override(160, 200)
        .centerCrop()
        .into(this)
}

fun ImageView.loadGridImage(@DrawableRes resourceId: Int) {
    TvGlide.with(context).load(resourceId)
        .placeholder(R.drawable.picture_icon_placeholder)
        .override(160, 200)
        .centerCrop()
        .into(this)
}

fun ImageView.loadIcon(@DrawableRes resourceId: Int) {
    TvGlide.with(context).load(resourceId)
        .placeholder(R.drawable.picture_icon_placeholder)
        .override(80, 80)
        .centerInside()
        .into(this)
}

fun ImageView.loadImage(url: String) {
    TvGlide.with(context).load(url)
        .placeholder(R.drawable.picture_icon_placeholder)
        .centerCrop()
        .into(this)
}

suspend fun Context.getDrawable(url: String): Drawable {
    return TvGlide.with(this).getDrawable(url)
}

suspend fun Fragment.getDrawable(url: String): Drawable {
    return TvGlide.with(this).getDrawable(url)
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

suspend fun Context.getBitmap(url: String): Bitmap {
    return TvGlide.with(this).getBitmap(url)
}

suspend fun Fragment.getBitmap(url: String): Bitmap {
    return TvGlide.with(this).getBitmap(url)
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

@GlideModule(glideName = "TvGlide")
open class TvGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDefaultRequestOptions(
            RequestOptions().format(DecodeFormat.PREFER_RGB_565)
        )

        val calculator = MemorySizeCalculator.Builder(context)
            .setMemoryCacheScreens(2f)
            .build()
        builder.setMemoryCache(LruResourceCache(calculator.memoryCacheSize.toLong()))
    }

    // 禁止解析Manifest文件,提升初始化速度，避免一些潜在错误
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    // 注册自定义组件
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory()
        )
    }
}