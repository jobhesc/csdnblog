package com.hesc.csdnblog.application;

import android.app.Application;
import android.util.Log;

/**
 * Created by Administrator on 2015/5/3 0003.
 */
public class MyApplication extends Application {
    private static final String LOG_TAG=MyApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
            }
        });
    }

}
