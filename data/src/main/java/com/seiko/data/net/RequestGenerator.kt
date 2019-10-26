package com.seiko.data.net

//import com.google.gson.Gson
//import com.seiko.data.pref.PrefHelper
//import okhttp3.HttpUrl
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.util.concurrent.TimeUnit
//
//private const val API_BASE_URL = "https://api.acplay.net/"
//private const val RES_BASE_URL = "http://res.acplay.net/"
//private const val DOWNLOAD_BASE_URL = "https://m2t.chinacloudsites.cn/"
//private const val SUBTITLE_BASE_URL = "https://dandanplay.com/"
//
//class ApiRequestGenerator(okHttpClient: OkHttpClient, gson: Gson) {
//
//    private val builder = Retrofit.Builder()
//        .baseUrl(API_BASE_URL)
//        .client(okHttpClient)
//        .addConverterFactory(GsonConverterFactory.create(gson))
//
//    fun <S> createService(serviceClass: Class<S>): S {
//        val retrofit = builder.build()
//        return retrofit.create(serviceClass)
//    }
//}
//
//class ResRequestGenerator(okHttpClient: OkHttpClient, gson: Gson) {
//
//    private val builder = Retrofit.Builder()
//        .baseUrl(RES_BASE_URL)
//        .client(okHttpClient)
//        .addConverterFactory(GsonConverterFactory.create(gson))
//
//    fun <S> createService(serviceClass: Class<S>): S {
//        val retrofit = builder.build()
//        return retrofit.create(serviceClass)
//    }
//}
//
//class DownloadRequestGenerator(okHttpClient: OkHttpClient, gson: Gson) {
//
//    private val builder = Retrofit.Builder()
//        .baseUrl(DOWNLOAD_BASE_URL)
//        .client(okHttpClient)
//        .addConverterFactory(GsonConverterFactory.create(gson))
//
//    fun <S> createService(serviceClass: Class<S>): S {
//        val retrofit = builder.build()
//        return retrofit.create(serviceClass)
//    }
//}
//
//class SubtitleRequestGenerator(okHttpClient: OkHttpClient, gson: Gson) {
//
//    private val builder = Retrofit.Builder()
//        .baseUrl(SUBTITLE_BASE_URL)
//        .client(okHttpClient)
//        .addConverterFactory(GsonConverterFactory.create(gson))
//
//    fun <S> createService(serviceClass: Class<S>): S {
//        val retrofit = builder.build()
//        return retrofit.create(serviceClass)
//    }
//}