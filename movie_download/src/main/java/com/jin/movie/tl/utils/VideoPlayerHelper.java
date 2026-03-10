package com.jin.movie.tl.utils;

import androidx.media3.datasource.DataSource;
import android.content.Context;
import androidx.media3.common.MediaItem;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import com.google.common.net.HttpHeaders;
import java.util.HashMap;



public class VideoPlayerHelper {

    private static final String TAG = "VideoPlayerHelper";

    public static DataSource.Factory createDataSourceFactory(Context context) {
        SignedHttpDataSourceFactory allowCrossProtocolRedirects = new SignedHttpDataSourceFactory("AiYa/3.9.5.9 (Android)", new TsSignGenerator()).setConnectTimeoutMs(10000).setReadTimeoutMs(10000).setAllowCrossProtocolRedirects(true);
        HashMap hashMap = new HashMap();
        hashMap.put(HttpHeaders.REFERER, "https://video.taolu.app");
        hashMap.put("token", "aiya_e39337b3-2ffd-41f1-9863-c987854bae9evq");
        allowCrossProtocolRedirects.setDefaultRequestProperties(hashMap);
        return new DefaultDataSource.Factory(context, allowCrossProtocolRedirects);
    }

    public static MediaSource createMediaSource(MediaItem mediaItem, DataSource.Factory factory, boolean z) {
        if (z) {
            return new HlsMediaSource.Factory(factory).createMediaSource(mediaItem);
        }
        return new ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem);
    }





}
