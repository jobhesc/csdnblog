package com.hesc.csdnblog.data;

import android.content.Context;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.application.MyApplication;
import com.hesc.csdnblog.utils.ConfigUtils;

import java.util.Arrays;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by hesc on 15/5/13.
 * 初始化数据装载器
 */
public class InitLoader {
    private volatile static InitLoader instance = null;
    private String[] initBlogIDs = null;  //初始化博客ID
    private boolean isLoading = false;
    private Subscription subscription;

    private InitLoader(Context context){
        initBlogIDs = context.getResources().getStringArray(R.array.init_blog_id);
    }

    public static InitLoader getInstance(){
        if(instance == null){
            synchronized (InitLoader.class){
                if(instance == null)
                    instance = new InitLoader(MyApplication.getContext());
            }
        }
        return instance;
    }

    /**
     * 是否已经进行过初始化数据装载
     * @return
     */
    public boolean isInitLoaded(){
        return ConfigUtils.isInitLoaded();
    }

    /**
     * 执行数据装载
     * @param callback 数据装载后的回调接口
     */
    public void execute(DataloadCallback callback){
        //已经进行过数据初始化操作，则不处理
        if(ConfigUtils.isInitLoaded()) return;
        //取消上一次的数据装载
        cancel();
        if(initBlogIDs != null && initBlogIDs.length>0) {
            subscription = BlogProvider.getInstance().loadBloggersFromNet(Arrays.asList(initBlogIDs))
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(
                            r -> {
                            //设置已经处理过数据初始化操作状态
                            ConfigUtils.setIsInitLoaded(true);
                            if(callback != null)
                                callback.dataLoaded();
                            },
                            e -> {
                                if(callback != null)
                                    callback.dataLoadFail();
                            }
            );
        }
    }

    /**
     * 取消数据装载
     */
    public void cancel(){
        if(subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}
