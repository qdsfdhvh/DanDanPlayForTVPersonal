package com.seiko.common.imageloader

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.io.File
import java.io.InputStream

@GlideModule(glideName = "TvGlide")
open class TvGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        GlobalScope.launch(Dispatchers.IO) {
            // 读写外部缓存目录不需要申请存储权限
            val diskCacheFile = File(context.cacheDir, "glide")
            // 如果这个路径是一个文件
            if (diskCacheFile.exists() && diskCacheFile.isFile) {
                // 执行删除操作
                diskCacheFile.delete()
            }
            // 如果这个路径不存在
            if (!diskCacheFile.exists()) {
                // 创建多级目录
                diskCacheFile.mkdirs()
            }
            builder.setDiskCache {
                DiskLruCacheWrapper.create(
                    diskCacheFile, IMAGE_DISK_CACHE_MAX_SIZE
                )
            }
        }

        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_RGB_565))

        val calculator = MemorySizeCalculator.Builder(context).build()
        val defaultMemoryCacheSize = calculator.memoryCacheSize
        val defaultBitmapPoolSize = calculator.bitmapPoolSize
        val customMemoryCacheSize = (1.2 * defaultMemoryCacheSize).toInt()
        val customBitmapPoolSize = (1.2 * defaultBitmapPoolSize).toInt()
        builder.setMemoryCache(LruResourceCache(customMemoryCacheSize.toLong()))
        builder.setBitmapPool(LruBitmapPool(customBitmapPoolSize.toLong()))
    }

    // 禁止解析Manifest文件,提升初始化速度，避免一些潜在错误
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    // 注册自定义组件
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context, TvGlideModuleEntryPoint::class.java
        )

        // Glide 默认采用的是 HttpURLConnection 来做网络请求，这里切换成更高效的 OkHttp
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpLoader.Factory(entryPoint.builder.build())
        )
    }

    companion object {
        /** 本地图片缓存文件最大值  */
        private const val IMAGE_DISK_CACHE_MAX_SIZE = 100 * 1024 * 1024L
    }
}

@InstallIn(ApplicationComponent::class)
@EntryPoint
interface TvGlideModuleEntryPoint {
    val builder: OkHttpClient.Builder
}
