package com.seiko.data.di

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.seiko.data.net.*
import com.seiko.data.net.cookie.CookiesManager
import com.seiko.data.net.cookie.PersistentCookieStore
import com.seiko.data.pref.PrefHelper
import com.tencent.mmkv.MMKV
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File
import java.util.concurrent.TimeUnit

const val API_DEFAULT  = "API_DEFAULT"
const val API_RES      = "API_RES"
const val API_DOWNLOAD = "API_DOWNLOAD"
//const val API_SUBTITLE = "API_SUBTITLE"

private const val HTTP_DEFAULT = "HTTP_DEFAULT"
private const val HTTP_SINGLE = "HTTP_SINGLE"
//private const val HTTP_SUBTITLE = "HTTP_SUBTITLE"

val networkModel = module {

    single { createCache(androidContext()) }

    single { createCookieManager(get()) }

    single(named(HTTP_SINGLE)) { createSingleHttpClient(get()) }

    single(named(HTTP_DEFAULT)) { createDefaultHttpClient(get(), get(), get()) }

//    single(named(HTTP_SUBTITLE)) { createSubtitleHttpClient(get(), get()) }

    single(named(API_DEFAULT)) {
        ApiRequestGenerator(get(named(HTTP_DEFAULT)), get())
            .createService(DanDanApiService::class.java)
    }

    single(named(API_RES)) {
        ResRequestGenerator(get(named(HTTP_SINGLE)), get())
            .createService(DanDanApiService::class.java)
    }

    single(named(API_DOWNLOAD)) {
        DownloadRequestGenerator(get(named(HTTP_SINGLE)), get())
            .createService(DanDanApiService::class.java)
    }

//    single(named(API_SUBTITLE)) {
//        SubtitleRequestGenerator(get(named(HTTP_SUBTITLE)), get())
//            .createService(DanDanApiService::class.java)
//    }

}

private fun createCache(context: Context): Cache {
    val file = File(context.cacheDir, "HttpResponseCache")
    return Cache(file, 10 * 1024 * 1024)
}

private fun createCookieManager(cookieStore: PersistentCookieStore): CookiesManager {
    return CookiesManager(cookieStore)
}

private fun createSingleHttpClient(cache: Cache): OkHttpClient {
    return OkHttpClient.Builder()
        .cache(cache)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()
}

private fun createDefaultHttpClient(cache: Cache,
                                    cookiesManager: CookiesManager,
                                    prefHelper: PrefHelper): OkHttpClient {
    return OkHttpClient.Builder()
        .cache(cache)
        .cookieJar(cookiesManager)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor(GzipInterceptor())
        .addInterceptor { chain ->
            val token = prefHelper.token
            if (token.isNotEmpty()) {
                val original = chain.request()
                val builder = original.newBuilder()
                    .header("Authorization", "Bearer $token")
                return@addInterceptor chain.proceed(builder.build())
            }
            return@addInterceptor chain.proceed(chain.request())
        }
        .build()
}

//private fun createSubtitleHttpClient(cache: Cache,  cookiesManager: CookiesManager): OkHttpClient {
//    return OkHttpClient.Builder()
//        .cache(cache)
//        .cookieJar(cookiesManager)
//        .connectTimeout(15, TimeUnit.SECONDS)
//        .readTimeout(10, TimeUnit.SECONDS)
//        .writeTimeout(10, TimeUnit.SECONDS)
//        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//        .addInterceptor { chain ->
//            val oldRequest = chain.request()
//            val newRequest = oldRequest.newBuilder()
//            val headerValues = oldRequest.headers("query")
//
//            val newBaseUrl: HttpUrl? = if (headerValues.size > 0 && "shooter" == headerValues[0]) {
//                newRequest.removeHeader("query")
//                HttpUrl.parse("https://www.shooter.cn/")
//            } else {
//                newRequest.removeHeader("query")
//                HttpUrl.parse("http://sub.xmp.sandai.net:8000/")
//            }
//
//            if (newBaseUrl != null) {
//                val newUrl = oldRequest.url()
//                    .newBuilder()
//                    .scheme(newBaseUrl.scheme())
//                    .host(newBaseUrl.host())
//                    .port(newBaseUrl.port())
//                    .build();
//                return@addInterceptor chain.proceed(newRequest.url(newUrl).build());
//            }
//            return@addInterceptor chain.proceed(oldRequest)
//        }
//        .build()
//}
