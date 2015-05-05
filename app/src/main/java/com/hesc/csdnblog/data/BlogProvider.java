package com.hesc.csdnblog.data;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by hesc on 15/5/4.
 */
public class BlogProvider {
    private static final String LOG_TAG=BlogProvider.class.getName();

    private Context mContext = null;
    private BlogDBHelper mDBHelper = null;
    private Executor dbExecutor;
    private Executor httpExecutor;
    private Hashtable<String, BlogHtmlParser> mHtmlParserPool;

    public BlogProvider(Context context){
        mContext = context;
        mDBHelper = new BlogDBHelper(mContext);
        mHtmlParserPool = new Hashtable<>();
        httpExecutor = Executors.newCachedThreadPool();
        dbExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * 根据博客ID装载博主信息
     * @param blogID
     * @return
     */
    public Observable<Blogger> loadBlogger(String blogID){
        return loadBloggerFromDB(blogID).flatMap(blogger -> loadBloggerFromNet(blogger));
    }

    /**
     * 装载所有的博主信息
     * @return
     */
    public Observable<List<Blogger>> loadAllBloggers(){
        return loadAllBloggerFromDB().flatMap(bloggers -> loadBloggersFromNet(bloggers));
    }

    /**
     * 从网络装载博客ID对应的博主信息
     * @param blogger
     * @return
     */
    private Observable<Blogger> loadBloggerFromNet(Blogger blogger){
        return Observable.create(subscriber -> {
            Runnable runnable = getLoadBloggerFromNet(subscriber, blogger);
            FutureTask<Void> task = new FutureTask<Void>(runnable, null);
            subscriber.add(Subscriptions.from(task));
            httpExecutor.execute(task);
        });
    }

    /**
     * 从数据库装载博客ID对应的博主信息
     * @return
     */
    private Observable<Blogger> loadBloggerFromDB(String blogID){
        return Observable.create(subscriber -> {
            Runnable runnable = getLoadBloggerFromDB(subscriber, blogID);
            FutureTask<Void> task = new FutureTask<Void>(runnable, null);
            subscriber.add(Subscriptions.from(task));
            dbExecutor.execute(task);
        });
    }

    /**
     * 从网络装载博主信息
     * @param bloggers
     * @return
     */
    private Observable<List<Blogger>> loadBloggersFromNet(List<Blogger> bloggers){
        return Observable.create(subscriber -> {
            Runnable runnable = getLoadBloggersFromNet(subscriber, bloggers);
            FutureTask<Void> task = new FutureTask<Void>(runnable, null);
            subscriber.add(Subscriptions.from(task));
            httpExecutor.execute(task);
        });
    }

    /**
     * 从数据库装载所有博主信息
     * @return
     */
    private Observable<List<Blogger>> loadAllBloggerFromDB(){
        return Observable.create(subscriber -> {
            Runnable runnable = getLoadBloggersFromDB(subscriber);
            FutureTask<Void> task = new FutureTask<Void>(runnable, null);
            subscriber.add(Subscriptions.from(task));
            dbExecutor.execute(task);
        });
    }

    /**
     * 根据博客ID号获取html解析器
     * @param blogID
     * @return
     */
    private BlogHtmlParser getHtmlParser(String blogID) {
        BlogHtmlParser htmlParser = mHtmlParserPool.get(blogID);
        if (htmlParser == null) {
            htmlParser = new BlogHtmlParser(blogID);
            mHtmlParserPool.put(blogID, htmlParser);
        }
        return htmlParser;
    }


    /**
     * 从网络上加载，更新博主信息
     * @param srcBlogger
     * @return
     */
    private Blogger updateBlogger(Blogger srcBlogger){
        if(srcBlogger == null) return null;
        try {
            //网络加载网页，调用html解析器进行解析
            BlogHtmlParser htmlParser = getHtmlParser(srcBlogger.blogID);
            htmlParser.parse();
            Blogger dstBlogger = htmlParser.getBlogger();

            if (dstBlogger != null) {
                //id
                dstBlogger.id = srcBlogger.id;
                //博客文章
                dstBlogger.articles = srcBlogger.articles;
            }
            return dstBlogger;
        } catch(Exception e){
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从网络加载所有博主信息的代码执行段
     * @param subscriber
     * @param bloggers
     * @return
     */
    private Runnable getLoadBloggersFromNet(final Subscriber<? super List<Blogger>> subscriber, List<Blogger> bloggers){
        return ()->{
            try {
                if (subscriber.isUnsubscribed()) {
                    return;
                }

                List<Blogger> results = new ArrayList<>();
                if (bloggers != null && bloggers.size() > 0) {
                    Blogger dstBlogger = null;
                    for (Blogger srcBlogger : bloggers) {
                        //从网络更新博主信息
                        dstBlogger = updateBlogger(srcBlogger);
                        results.add(dstBlogger==null?srcBlogger:dstBlogger);
                    }
                }
                //保存最新的博主信息到数据库
                mDBHelper.updateBloggers(results);

                subscriber.onNext(results);
                subscriber.onCompleted();
            } catch(Exception e){
                subscriber.onError(e);
            }
        };
    }

    /**
     * 从数据库查询所有博主信息的代码执行段
     * @param subscriber
     * @return
     */
    private Runnable getLoadBloggersFromDB(final Subscriber<? super List<Blogger>> subscriber){
        return ()->{
            try {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                //从数据库查询所有的博主信息
                List<Blogger> bloggers = mDBHelper.findAllBloggers();
                subscriber.onNext(bloggers);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        };
    }

    /**
     * 从网络加载所有博主信息的代码执行段
     * @param subscriber
     * @param srcBlogger
     * @return
     */
    private Runnable getLoadBloggerFromNet(final Subscriber<? super Blogger> subscriber, Blogger srcBlogger){
        return ()->{
            try {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                //从网络更新博主信息
                Blogger dstBlogger = updateBlogger(srcBlogger);
                //保存最新的博主信息到数据库
                if(dstBlogger != null){
                    mDBHelper.insertOrUpdateBlogger(dstBlogger);
                }

                subscriber.onNext(dstBlogger == null?srcBlogger:dstBlogger);
                subscriber.onCompleted();
            } catch(Exception e){
                subscriber.onError(e);
            }
        };
    }

    /**
     * 从数据库查询博客ID对应的博主信息的代码执行段
     * @param subscriber
     * @param bloggID
     * @return
     */
    private Runnable getLoadBloggerFromDB(final Subscriber<? super Blogger> subscriber, String bloggID){
        return ()->{
            try {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                //从数据库查询博客ID对应的博主信息
                Blogger blogger = mDBHelper.findBlogger(bloggID);
                if(blogger == null){
                    blogger = new Blogger();
                    blogger.blogID = bloggID;
                }
                subscriber.onNext(blogger);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        };
    }
}
