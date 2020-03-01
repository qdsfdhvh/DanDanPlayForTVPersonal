package com.seiko.player.data.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GzipInterceptor implements Interceptor {

        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            if (response.code() == 200 && response.body() != null) {
                MediaType mediaType = response.body().contentType();
                byte[] data = response.body().bytes();
                if (GzipUtils.isGzip(response.headers())) {
                    data = GzipUtils.uncompress(data);
                }
                return response.newBuilder()
                        .body(ResponseBody.create(mediaType, data))
                        .build();
            } else {
                return response;
            }

        }
    }