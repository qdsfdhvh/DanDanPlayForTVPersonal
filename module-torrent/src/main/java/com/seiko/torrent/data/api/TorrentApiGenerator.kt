package com.seiko.torrent.data.api

import com.seiko.torrent.util.constants.DOWNLOAD_BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit

internal class TorrentApiGenerator(okHttpClient: OkHttpClient) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(DOWNLOAD_BASE_URL)
        .client(okHttpClient)

    fun create(): TorrentApiService {
        return retrofit.build().create(TorrentApiService::class.java)
    }

}