package com.hesc.csdnblog.data;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.hesc.csdnblog.application.MyApplication;
import com.squareup.okhttp.internal.DiskLruCache;
import com.squareup.okhttp.internal.io.FileSystem;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okio.Buffer;
import okio.Sink;
import okio.Source;

/**
 * Created by hesc on 2015/5/15 0015.
 */
public class ArticleCache {
    private volatile static ArticleCache instance = null;

    private static final String LOG_TAG=ArticleCache.class.getName();
    private static final String CACHE_FILE = "article";     //缓存目录名
    private static final int CACHE_MAX_SIZE = 50*1024*1024; //缓存大小=50M
    private File mCacheDir;    //缓存目录
    private Context mContext;
    private DiskLruCache mCache;
    private ArticleCache(Context context){
        mContext = context;
        //构建缓存目录
        buildCacheDir();
        //构建DiskLruCache
        mCache = DiskLruCache.create(FileSystem.SYSTEM, mCacheDir, getAppVersion(), 1, CACHE_MAX_SIZE);
    }

    public static ArticleCache getInstance(){
        if(instance == null){
            synchronized (ArticleCache.class){
                if(instance == null)
                    instance = new ArticleCache(MyApplication.getContext());
            }
        }
        return instance;
    }

    /**
     * 把博客文章内容放到缓存中
     * @param articleUrl 博客文章url
     * @param articleContent 博客文章内容
     */
    public void put(String articleUrl, String articleContent){
        try {
            DiskLruCache.Editor editor = mCache.edit(getCacheKey(articleUrl));
            Sink sink = editor.newSink(0);
            byte[] bytes = string2Bytes(articleContent);

            Buffer buffer = new Buffer();
            buffer.write(bytes);
            sink.write(buffer, buffer.size());
            editor.commit();

            sink.close();
            buffer.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    /**
     * 根据博客文章url获取博客文章内容
     * @param articleUrl
     * @return
     */
    public String get(String articleUrl){
        try {
            DiskLruCache.Snapshot snapshot = mCache.get(getCacheKey(articleUrl));
            if(snapshot == null) return "";
            Source source = snapshot.getSource(0);
            Buffer buffer = new Buffer();
            while(true){
                long len = source.read(buffer, 2048);
                if(len<=0)
                    break;
            }
            String articleContent = new String(buffer.readByteArray(), getCharset());
            buffer.close();
            source.close();
            return articleContent;
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return "";
        }
    }

    /**
     * 获取缓存大小，单位：byte
     * @return
     */
    public long getCacheSize(){
        try {
            return mCache.size();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 清除缓存
     */
    public void clear(){
        try {
            mCache.delete();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private byte[] string2Bytes(String s){
        return s.getBytes(getCharset());
    }

    private Charset getCharset(){
        return Charset.forName("utf-8");
    }

    /**
     * 根据文章目录获取缓存key
     * @param articleUrl
     * @return
     */
    private String getCacheKey(String articleUrl){
        try {
            //使用md对路径进行加密，获取加密后的字符串作为key
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(string2Bytes(articleUrl));
            int count = bytes.length;
            StringBuilder builder = new StringBuilder();
            String hex;
            for(int i=0; i<count; i++){
                hex = Integer.toHexString(bytes[i] & 0xff);
                if(hex.length() == 1)
                    builder.append(0);
                builder.append(hex);
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return articleUrl.hashCode()+"";
        }
    }

    /**
     * 构建缓存目录
     */
    private void buildCacheDir(){
        if(!Environment.isExternalStorageRemovable() &&
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            mCacheDir = new File(mContext.getExternalCacheDir(),CACHE_FILE);
        } else {
            mCacheDir = new File(mContext.getCacheDir(), CACHE_FILE);
        }

        if(!mCacheDir.exists())
            mCacheDir.mkdirs();
    }

    /**
     * 获取应用程序版本
     * @return
     */
    private int getAppVersion(){
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取缓存目录
     * @return
     */
    public File getCacheDir(){
        return mCacheDir;
    }
}
