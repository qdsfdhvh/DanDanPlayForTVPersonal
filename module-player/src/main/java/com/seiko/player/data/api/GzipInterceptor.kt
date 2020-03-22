package com.seiko.player.data.api

import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.BufferedSource
import okio.buffer
import okio.gzip
import java.io.IOException
import java.net.HttpURLConnection

class GzipInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == HttpURLConnection.HTTP_OK) {
            val body = response.body
            if (body != null && isGzip(response.headers)) {
                return response.newBuilder()
                    .body(gzip(body))
                    .build()
            }
        }
        return response
    }

    private fun isGzip(headers: Headers): Boolean {
        return "gzip".equals(headers["Content-Encoding"], ignoreCase = true)
                || "gzip".equals(headers["Accept-Encoding"], ignoreCase = true)
    }

    private fun gzip(body: ResponseBody): ResponseBody {
        return object : ResponseBody() {
            override fun contentType(): MediaType? {
                return body.contentType()
            }

            override fun contentLength(): Long {
                return -1L
            }

            override fun source(): BufferedSource {
                return body.source().gzip().buffer()
            }
        }
    }
}