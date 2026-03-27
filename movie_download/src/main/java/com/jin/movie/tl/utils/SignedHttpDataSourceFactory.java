package com.jin.movie.tl.utils;

import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSpec;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.datasource.TransferListener;

import com.google.common.base.Predicate;
import com.google.common.net.HttpHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignedHttpDataSourceFactory implements HttpDataSource.Factory{


    private static final String TAG = "SignedHttpDataSource";
    private final DefaultHttpDataSource.Factory officialFactory;
    private final SignGenerator signGenerator;

    public interface SignGenerator {
        String generateSign(String str);
    }

    @OptIn(markerClass = UnstableApi.class)
    public SignedHttpDataSourceFactory(String str, SignGenerator signGenerator2) {
        this.signGenerator = signGenerator2;
        DefaultHttpDataSource.Factory factory = new DefaultHttpDataSource.Factory();
        this.officialFactory = factory;
        factory.setUserAgent(str).setConnectTimeoutMs(8000).setReadTimeoutMs(8000).setAllowCrossProtocolRedirects(false).setKeepPostFor302Redirects(false);
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("User-Agent 不能为空");
        } else if (signGenerator2 == null) {
            throw new IllegalArgumentException("SignGenerator 不能为空");
        }
    }



    @OptIn(markerClass = UnstableApi.class) @Override
    public HttpDataSource createDataSource() {
        final DefaultHttpDataSource createDataSource = this.officialFactory.createDataSource();
        return new HttpDataSource() {
            @Override
            public long open(DataSpec dataSpec) throws HttpDataSourceException {
                String uri = dataSpec.uri.toString();
                HashMap hashMap = new HashMap();
                for (String str : dataSpec.httpRequestHeaders.keySet()) {
                    hashMap.put(str, dataSpec.httpRequestHeaders.get(str));
                }
                if (!hashMap.containsKey(HttpHeaders.REFERER)) {
                    hashMap.put(HttpHeaders.REFERER, ConstPools.referer);
                }
                if (!hashMap.containsKey("token")) {
                    hashMap.put("token", ConstPools.token);
                }
                if (SignedHttpDataSourceFactory.this.isTsFile(uri)) {
                    uri = SignedHttpDataSourceFactory.this.signGenerator.generateSign(uri);
                } else {
                }
                return createDataSource.open(new DataSpec.Builder().setUri(uri).setUriPositionOffset(dataSpec.uriPositionOffset).setHttpMethod(dataSpec.httpMethod).setHttpBody(dataSpec.httpBody).setHttpRequestHeaders(hashMap).setPosition(dataSpec.position).setLength(dataSpec.length).setKey(dataSpec.key).setFlags(dataSpec.flags).setCustomData(dataSpec.customData).build());

            }

            @Override
            public void close() throws HttpDataSourceException {
                createDataSource.close();
            }

            @Override
            public int read(byte[] buffer, int offset, int length) throws HttpDataSourceException {
                return createDataSource.read(buffer, offset, length);
            }

            @Override
            public void setRequestProperty(String name, String value) {
                createDataSource.setRequestProperty(name, value);
            }

            @Override
            public void clearRequestProperty(String name) {
                createDataSource.clearRequestProperty(name);
            }

            @Override
            public void clearAllRequestProperties() {
                createDataSource.clearAllRequestProperties();
            }

            @Override
            public int getResponseCode() {
                return createDataSource.getResponseCode();
            }

            @Override
            public Map<String, List<String>> getResponseHeaders() {
                return createDataSource.getResponseHeaders();
            }

            @Override
            public void addTransferListener(TransferListener transferListener) {
                createDataSource.addTransferListener(transferListener);
            }

            @Nullable
            @Override
            public Uri getUri() {
                return createDataSource.getUri();
            }
        };
    }

    @OptIn(markerClass = UnstableApi.class) @Override
    public HttpDataSource.Factory setDefaultRequestProperties(Map<String, String> defaultRequestProperties) {
        this.officialFactory.setDefaultRequestProperties(defaultRequestProperties);
        return this;
    }


    public SignedHttpDataSourceFactory setUserAgent(String str) {
        this.officialFactory.setUserAgent(str);
        return this;
    }

    @OptIn(markerClass = UnstableApi.class) public SignedHttpDataSourceFactory setConnectTimeoutMs(int i) {
        this.officialFactory.setConnectTimeoutMs(i);
        return this;
    }

    @OptIn(markerClass = UnstableApi.class) public SignedHttpDataSourceFactory setReadTimeoutMs(int i) {
        this.officialFactory.setReadTimeoutMs(i);
        return this;
    }

    @OptIn(markerClass = UnstableApi.class) public SignedHttpDataSourceFactory setAllowCrossProtocolRedirects(boolean z) {
        this.officialFactory.setAllowCrossProtocolRedirects(z);
        return this;
    }

    @OptIn(markerClass = UnstableApi.class) public SignedHttpDataSourceFactory setContentTypePredicate(Predicate<String> predicate) {
        this.officialFactory.setContentTypePredicate(predicate);
        return this;
    }

    @OptIn(markerClass = UnstableApi.class) public SignedHttpDataSourceFactory setTransferListener(TransferListener transferListener) {
        this.officialFactory.setTransferListener(transferListener);
        return this;
    }

    @OptIn(markerClass = UnstableApi.class) public SignedHttpDataSourceFactory setKeepPostFor302Redirects(boolean z) {
        this.officialFactory.setKeepPostFor302Redirects(z);
        return this;
    }


    private boolean isTsFile(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        String lowerCase = str.toLowerCase();
        if (lowerCase.endsWith(".ts") || lowerCase.contains(".ts?") || lowerCase.contains(".ts&")) {
            return true;
        }
        return false;
    }



}
