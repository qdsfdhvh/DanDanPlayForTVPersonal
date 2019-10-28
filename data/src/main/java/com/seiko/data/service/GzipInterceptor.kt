package com.seiko.data.service

//import com.seiko.data.utils.GZIPUtils
//import okhttp3.Interceptor
//import okhttp3.Response
//import okhttp3.ResponseBody
//
//class GzipInterceptor : Interceptor {
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//        val response = chain.proceed(request)
//        if (response.code() == 200) {
//            val body = response.body()
//            if (body != null) {
//                val mediaType = body.contentType()
//                var data = body.bytes()
//                if (GZIPUtils.isGzip(response.headers())) {
//                    data = GZIPUtils.uncompress(data)
//                }
//                return response.newBuilder()
//                    .body(ResponseBody.create(mediaType, data))
//                    .build()
//            }
//        }
//        return response
//    }
//
//}