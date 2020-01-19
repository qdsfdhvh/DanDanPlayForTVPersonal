package com.seiko.core.http.interceptor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * <p>
 * An OkHttp interceptor which cache to Response,
 * Allow your application to have offline caching function
 * ,this cache Valid only for Get
 * <p>
 * User: chengwangyong(chengwangyong@blinnnk.com)
 * Date: 2017/7/18
 * Time: 上午11:00
 */
public class RetrofitCacheInterceptor implements Interceptor {
    private Context context;
    private final String pragma = "pragma";
    private final String cacheControl = "Cache-Control";

    private volatile int maxAge = 60 * 60;
    private volatile int maxStale = 60 * 60 * 24 * 28;
    private String maxStaleString = "public, only-if-cached, max-stale=" + maxStale;
    private String maxAgeString = "public, only-if-cached, max-stale=" + maxAge;


    public RetrofitCacheInterceptor(Context context) {
        this.context = context;
    }

    /**
     * set this response cache date in Network connection, Units are milliseconds
     * <p>
     * default is 60 * 60
     *
     * @param maxAge response cache date in Network connection
     * @return this
     */
    public RetrofitCacheInterceptor setMaxAge(int maxAge) {
        this.maxAge = maxAge;
        maxAgeString = "public, only-if-cached, max-stale=" + maxAge;
        return this;
    }

    /**
     * set this response cache date in Network not connection, Units are milliseconds
     * <p>
     * default is 60 * 60 * 24 * 28 4week,Of course,you can custom a long time
     *
     * @param maxStale response cache date in Network not connection
     * @return this
     */
    public RetrofitCacheInterceptor setMaxStale(int maxStale) {
        this.maxStale = maxStale;
        maxStaleString = "public, only-if-cached, max-stale=" + maxStale;
        return this;
    }

    @NotNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (isNetworkReachable(context)) {
            Response response = chain.proceed(request);
            // read from cache for 1 minute
            return response.newBuilder()
                    .removeHeader(pragma)
                    .header(cacheControl, maxAgeString)
                    .build();
        } else {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();

            Response response = chain.proceed(request);
            // read from cache for 1 minute
            return response.newBuilder()
                    .removeHeader(pragma)
                    .header(cacheControl, maxAgeString)
                    .build();
        }
    }

    private synchronized static Boolean isNetworkReachable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = cm.getActiveNetworkInfo();
        return current != null && (current.isAvailable());
    }
}