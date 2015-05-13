package com.hesc.csdnblog.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hesc.csdnblog.application.MyApplication;

/**
 * Created by hesc on 15/5/13.
 */
public class ConfigUtils {
    private static final String CONFIG_FILE="config";
    private static Context mContext =MyApplication.getContext();

    private static SharedPreferences getPref(){
        return mContext.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
    }

    /**
     * 获取是否第一次使用系统
     * @return
     */
    public static boolean isFirstTimeUse(){
        return getPref().getBoolean("firstTimeUse", true);
    }

    /**
     * 设置是否第一次使用系统
     * @param firstTimeUse
     */
    public static void setIsFirstTimeUse(boolean firstTimeUse){
        getPref().edit().putBoolean("firstTimeUse", firstTimeUse).apply();
    }

    /**
     * 是否已经进行过装载初始化数据
     * @return
     */
    public static boolean isInitLoaded(){
        return getPref().getBoolean("initLoaded", false);
    }

    /**
     * 设置是否已经进行过装载初始化数据操作
     * @param initLoaded
     */
    public static void setIsInitLoaded(boolean initLoaded){
        getPref().edit().putBoolean("initLoaded", initLoaded).apply();
    }
}
