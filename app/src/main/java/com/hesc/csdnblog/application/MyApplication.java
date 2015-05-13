package com.hesc.csdnblog.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by Administrator on 2015/5/3 0003.
 */
public class MyApplication extends Application {
    private static final String LOG_TAG=MyApplication.class.getName();
    private static Context globalContext;

    @Override
    public void onCreate() {
        super.onCreate();
        globalContext = this.getApplicationContext();
        //初始化ImageLoader
        initImageLoader(getApplicationContext());

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
            }
        });
    }

    public static Context getContext() {
        return globalContext;
    }

    /**
     * 初始化ImageLoader
     * @param context
     */
    private void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .resetViewBeforeLoading(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(options)
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

}
