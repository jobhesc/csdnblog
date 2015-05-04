package com.hesc.csdnblog.application;

import android.content.Context;

import com.hesc.csdnblog.data.BlogDBUtils;

/**
 * Created by hesc on 15/5/4.
 */
public class AppIntfProvider {
    private Context context = null;
    private BlogDBUtils mDBProvider = null;

    private static class AppIntfProviderHolder{
        private static AppIntfProvider instance = new AppIntfProvider();
    }

    private AppIntfProvider(){}

    public static AppIntfProvider getInstance(){
        return AppIntfProviderHolder.instance;
    }

    /**
     * 设置上下文参数，由于本类提供全局性接口，因此context最好是application级的context
     * @param context
     */
    void setContext(Context context){
        this.context = context;
    }

    /**
     * 获取数据提供实现类
     * @return
     */
    public BlogDBUtils getDBProvider(){
        if(mDBProvider == null){
            mDBProvider = new BlogDBUtils(context);
        }
        return mDBProvider;
    }

}
