package com.wifi.exchangefile;

import android.app.Application;

/**
 * Created by Administrator on 2018/3/7 0007.
 */

public class MyApplication extends Application {
    private static MyApplication mInstance;


    public static MyApplication getmInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("Not yet initialized");
        }
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (mInstance != null) {
            throw new IllegalStateException("Not a singleton");
        }
        mInstance = this;
    }


}
