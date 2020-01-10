package com.seiko.data.di

import android.content.Context
import com.google.gson.Gson
import com.seiko.data.http.api.DanDanApiService
import com.seiko.data.http.api.ResDanDanApiService
import com.seiko.data.http.api.TorrentApiService
import com.seiko.data.http.cookie.CookiesManager
import com.seiko.data.http.cookie.PersistentCookieStore
import com.seiko.data.local.pref.PrefDataSource
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

const val API_DEFAULT  = "API_DEFAULT"
const val API_DOWNLOAD = "API_DOWNLOAD"
//const val API_SUBTITLE = "API_SUBTITLE"

private const val HTTP_DEFAULT = "HTTP_DEFAULT"
private const val HTTP_SINGLE = "HTTP_SINGLE"
//private const val HTTP_SUBTITLE = "HTTP_SUBTITLE"

private const val API_BASE_URL = "https://api.acplay.net/"
private const val RES_BASE_URL = "http://res.acplay.net/"
private const val DOWNLOAD_BASE_URL = "https://m2t.chinacloudsites.cn/"
private const val SUBTITLE_BASE_URL = "https://dandanplay.com/"

internal val networkModel = module {

    single { createCache(androidContext()) }

    single { createCookieManager(get()) }

    single(named(HTTP_SINGLE)) { createSingleHttpClient(get()) }

    single(named(HTTP_DEFAULT)) { createDefaultHttpClient(get(), get(), get()) }

    single(named(API_DEFAULT)) { createApiService(get(named(HTTP_DEFAULT)), get()) }

    single { createResApiService(get(named(HTTP_SINGLE)), get()) }

    single { createTorrentApiService(get(named(HTTP_SINGLE))) }

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
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
//        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
}

private fun createDefaultHttpClient(cache: Cache,
                                    cookiesManager: CookiesManager,
                                    prefHelper: PrefDataSource
): OkHttpClient {
    return OkHttpClient.Builder()
        .cache(cache)
        .cookieJar(cookiesManager)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//        .addInterceptor(GzipInterceptor())
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

private fun createApiService(okHttpClient: OkHttpClient, gson: Gson): DanDanApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    return retrofit.create(DanDanApiService::class.java)
}

private fun createResApiService(okHttpClient: OkHttpClient, gson: Gson): ResDanDanApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl(RES_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    return retrofit.create(ResDanDanApiService::class.java)
}

private fun createTorrentApiService(okHttpClient: OkHttpClient): TorrentApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl(DOWNLOAD_BASE_URL)
        .client(okHttpClient)
//        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    return retrofit.create(TorrentApiService::class.java)
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
