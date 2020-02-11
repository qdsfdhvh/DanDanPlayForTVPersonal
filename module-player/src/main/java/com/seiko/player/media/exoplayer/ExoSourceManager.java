package com.seiko.player.media.exoplayer;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheSpan;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * Created by KathLine on 2018/5/18.
 */

public class ExoSourceManager {

    private static final String TAG = "ExoSourceManager";

    private static final long DEFAULT_MAX_SIZE = 512 * 1024 * 1024;

    private static Cache mCache;

    private final Context mAppContext;
    private final OkHttpClient mOkHttpClient;

//    private Map<String, String> mMapHeadData = new HashMap<>();

    private String mDataSource;

    private boolean isCached = false;

    public static ExoSourceManager newInstance(Context context, OkHttpClient okHttpClient) {
        return new ExoSourceManager(context, okHttpClient);
    }

    private ExoSourceManager(Context context, OkHttpClient okHttpClient) {
        mAppContext = context.getApplicationContext();
        mOkHttpClient = okHttpClient;
    }

    public MediaSource getMediaSource(String dataSource, boolean preview, boolean cacheEnable, boolean isLooping, File cacheDir) {
        mDataSource = dataSource;
        Uri contentUri = Uri.parse(dataSource);
        int contentType = inferContentType(dataSource);
        MediaSource mediaSource;
        switch (contentType) {
            case C.TYPE_SS:
                mediaSource = new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(
                                getDataSourceFactoryCache(mAppContext, cacheEnable, preview, cacheDir)
                        ),
                        new DefaultDataSourceFactory(mAppContext, null,
                                getHttpDataSourceFactory(mAppContext, preview)
                        )
                ).createMediaSource(contentUri);
                break;
            case C.TYPE_DASH:
                mediaSource = new DashMediaSource.Factory(
                        new DefaultDashChunkSource.Factory(
                                getDataSourceFactoryCache(mAppContext, cacheEnable, preview, cacheDir)
                        ),
                        new DefaultDataSourceFactory(mAppContext, null,
                                getHttpDataSourceFactory(mAppContext, preview)
                        )
                ).createMediaSource(contentUri);
                break;
            case C.TYPE_HLS:
                mediaSource = new HlsMediaSource.Factory(
                        getDataSourceFactoryCache(mAppContext, cacheEnable, preview, cacheDir)
                ).createMediaSource(contentUri);
                break;
            case C.TYPE_OTHER:
            default:
                mediaSource = new ProgressiveMediaSource.Factory(
                        getDataSourceFactoryCache(mAppContext, cacheEnable, preview, cacheDir),
                        new DefaultExtractorsFactory()
                ).createMediaSource(contentUri);
                break;
        }
        if (isLooping) {
            return new LoopingMediaSource(mediaSource);
        }
        return mediaSource;
    }

    @C.ContentType
    private static int inferContentType(String fileName) {
        fileName = Util.toLowerInvariant(fileName);
        if (fileName.endsWith(".mpd")) {
            return C.TYPE_DASH;
        } else if (fileName.endsWith(".m3u8")) {
            return C.TYPE_HLS;
        } else if (fileName.endsWith(".ism") || fileName.endsWith(".isml")
                || fileName.endsWith(".ism/manifest") || fileName.endsWith(".isml/manifest")) {
            return C.TYPE_SS;
        }
        else {
            return C.TYPE_OTHER;
        }
    }

    /**
     * 本地缓存目录
     */
    private static synchronized Cache getCacheSingleInstance(Context context, File cacheDir) {
        String dirs = context.getCacheDir().getAbsolutePath();
        if (cacheDir != null) {
            dirs = cacheDir.getAbsolutePath();
        }
        if (mCache == null) {
            String path = dirs + File.separator + "exo";
            boolean isLocked = SimpleCache.isCacheFolderLocked(new File(path));
            if (!isLocked) {
                mCache = new SimpleCache(new File(path), new LeastRecentlyUsedCacheEvictor(DEFAULT_MAX_SIZE));
            }
        }
        return mCache;
    }

    public void release() {
        isCached = false;
        if (mCache != null) {
            try {
                mCache.release();
                mCache = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cache需要release之后才能clear
     *
     * @param context
     * @param cacheDir
     * @param url
     */
    public static void clearCache(Context context, File cacheDir, String url) {
        try {
            Cache cache = getCacheSingleInstance(context, cacheDir);
            if (!TextUtils.isEmpty(url)) {
                if (cache != null) {
                    CacheUtil.remove(cache, CacheUtil.generateKey(Uri.parse(url)));
                }
            } else {
                if (cache != null) {
                    for (String key : cache.getKeys()) {
                        CacheUtil.remove(cache, key);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean cachePreView(Context context, File cacheDir, String url) {
        return resolveCacheState(getCacheSingleInstance(context, cacheDir), url);
    }

    public boolean hadCached() {
        return isCached;
    }


    /**
     * 获取SourceFactory，是否带Cache
     */
    private DataSource.Factory getDataSourceFactoryCache(Context context, boolean cacheEnable, boolean preview, File cacheDir) {
        if (cacheEnable) {
            Cache cache = getCacheSingleInstance(context, cacheDir);
            if (cache != null) {
                isCached = resolveCacheState(cache, mDataSource);
                return new CacheDataSourceFactory(cache,
                        getDataSourceFactory(context, preview),
                        CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
            }
        }
        return getDataSourceFactory(context, preview);
    }

    /**
     * 获取SourceFactory
     */
    @NotNull
    private DataSource.Factory getDataSourceFactory(Context context, boolean preview) {
        return new DefaultDataSourceFactory(context,
                preview ? null : new DefaultBandwidthMeter.Builder(context).build(),
                getHttpDataSourceFactory(context, preview));
    }

    @NotNull
    private DataSource.Factory getHttpDataSourceFactory(Context context, boolean preview) {
        OkHttpDataSourceFactory dataSourceFactory = new OkHttpDataSourceFactory(
                mOkHttpClient,
                Util.getUserAgent(context, TAG),
                preview ? null : new DefaultBandwidthMeter.Builder(context).build()
        );
//        if (mMapHeadData != null && mMapHeadData.size() > 0) {
//            for (Map.Entry<String, String> header : mMapHeadData.entrySet()) {
//                dataSourceFactory.getDefaultRequestProperties().set(header.getKey(), header.getValue());
//            }
//        }
        return dataSourceFactory;
    }

    /**
     * 根据缓存块判断是否缓存成功
     *
     * @param cache 缓存
     */
    private static boolean resolveCacheState(Cache cache, String url) {
        boolean isCache = true;
        if (!TextUtils.isEmpty(url)) {
            String key = CacheUtil.generateKey(Uri.parse(url));
            if (!TextUtils.isEmpty(key)) {
                NavigableSet<CacheSpan> cachedSpans = cache.getCachedSpans(key);
                if (cachedSpans.size() == 0) {
                    isCache = false;
                } else {

                    long contentLength = cache.getCacheSpace();
                    long currentLength = 0;
                    for (CacheSpan cachedSpan : cachedSpans) {
                        currentLength += cache.getCachedLength(key, cachedSpan.position, cachedSpan.length);
                    }
                    isCache = currentLength >= contentLength;
                }
            } else {
                isCache = false;
            }
        }
        return isCache;
    }
}