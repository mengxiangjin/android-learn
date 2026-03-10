package com.jin.movie.tl.utils;

public class TsSignGenerator implements SignedHttpDataSourceFactory.SignGenerator {
    private static final String TAG = "TsSignGenerator";

    @Override // com.nuo1000.yoho.m3u8.SignedHttpDataSourceFactory.SignGenerator
    public String generateSign(String str) {
        return str + "?sign=" + SignUtils.INSTANCE.calculateSignature(str);
    }
}
