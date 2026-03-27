package com.jin.movie.tl.utils;

import android.util.Log;

public class TsSignGenerator implements SignedHttpDataSourceFactory.SignGenerator {
    private static final String TAG = "TsSignGenerator";

    @Override // com.nuo1000.yoho.m3u8.SignedHttpDataSourceFactory.SignGenerator
    public String generateSign(String str) {
        Log.d("TAG", "generateSign: "  + str);
        return str + "?sign=" + SignUtils.INSTANCE.calculateSignature(str);
    }
}
