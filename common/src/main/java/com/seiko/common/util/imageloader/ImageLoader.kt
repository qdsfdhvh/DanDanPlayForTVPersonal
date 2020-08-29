package com.seiko.common.util.imageloader

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView

interface ImageLoader {
    fun loadGridImage(view: ImageView, url: String)
    fun loadGridImage(view: ImageView, resId: Int)
    fun loadImage(view: ImageView, url: String)
    fun loadImage(view: ImageView, resId: Int)
    suspend fun getDrawable(url: String): Drawable?
    suspend fun getBitMap(url: String): Bitmap?
}
