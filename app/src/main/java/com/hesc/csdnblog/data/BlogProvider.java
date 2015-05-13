package com.hesc.csdnblog.data;

import android.content.Context;

import com.hesc.csdnblog.application.MyApplication;

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
    private volatile static BlogProvider instance;

    private BlogProvider(Context context){
        mContext = context;
        mDBHelper = new BlogDBHelper(mContext);
        mHtmlParserPool = new Hashtable<>();
        httpExecutor = Executors.newCachedThreadPool();
        dbExecutor = Executors.newSingleThreadExecutor();
    }

    public static BlogProvider getInstance(){
        if(instance == null){
            synchronized (BlogProvider.class){
                if(instance == null){
                    instance = new BlogProvider(MyApplication.getContext());
                }
            }
        }
        return instance;
    }

    /**
     * 从网络分页装载博主对应的博客文章
     * @param blogger
     * @param startIndex
     * @param requestLen
     * @return
     */
    public Observable<List<BlogArticle>> loadArticlesFromNet(Blogger blogger, int startIndex, int requestLen){
        return Observable.create(subscriber -> {
            Runnable runnable = getArticlesFromNet(subscriber, blogger, startIndex, requestLen);
            FutureTask<Void> task = new FutureTask<Void>(runnable, null);
            subscriber.add(Subscriptions.from(task));
            httpExecutor.execute(task);
        });
    }

    /**
     * 从数据库分页加载博主对应的博客文章
     * @param blogger
     * @param startIndex
     * @param requestLen
     * @return
     */
    public Observable<List<BlogArticle>> loadArticlesFromDB(Blogger blogger, int startIndex, int requestLen){
        return Observable.create(subscriber -> {
            Runnable runnable = getArticlesFromDB(subscriber, blogger, startIndex, requestLen);
            FutureTask<Void> task = new FutureTask<Void>(runnable, null);
            subscriber.add(Subscriptions.from(task));
            dbExecutor.execute(task);
        });
    }

    /**
     * 从网络装载博客ID对应的博主信息
     * @param blogID
     * @return
     */
    public Observable<Blogger> loadBloggerFromNet(String blogID){
        return Observable.create(subscriber -> {
            Runnable runnable = getLoadBloggerFromNet(subscriber, blogID);
            FutureTask<Void> task = new FutureTask<Void>(runnable, null);
            subscriber.add(Subscriptions.from(task));
            httpExecutor.execute(task);
        });
    }

    /**
     * 从数据库装载博客ID对应的博主信息
     * @return
     */
    public Observable<Blogger> loadBloggerFromDB(String blogID){
        return Observable.create(subscriber -> {
            Runnable runnable = getLoadBloggerFromDB(subscriber, blogID);
            FutureTask<Void> task = new FutureTask<Void>(runnable, null);
            subscriber.add(Subscriptions.from(task));
            dbExecutor.execute(task);
        });
    }

    /**
     * 从网络装载博主信息
     * @param blogIDs
     * @return
     */
    public Observable<List<Blogger>> loadBloggersFromNet(List<String> blogIDs){
        return Observable.create(subscriber -> {
            Runnable runnable = getLoadBloggersFromNet(subscriber, blogIDs);
            FutureTask<Void> task = new FutureTask<Void>(runnable, null);
            subscriber.add(Subscriptions.from(task));
            httpExecutor.execute(task);
        });
    }

    /**
     * 从数据库装载所有博主信息
     * @return
     */
    public Observable<List<Blogger>> loadAllBloggerFromDB(){
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
     * 从网络加载所有博主信息的代码执行段
     * @param subscriber
     * @param blogIDs
     * @return
     */
    private Runnable getLoadBloggersFromNet(final Subscriber<? super List<Blogger>> subscriber, List<String> blogIDs){
        return ()->{
            try {
                if (subscriber.isUnsubscribed()) {
                    return;
                }

                List<Blogger> results = new ArrayList<>();
                if (blogIDs != null && blogIDs.size() > 0) {
                    Blogger dstBlogger = null;
                    for (String blogID : blogIDs) {
                        //从网络更新博主信息
                        dstBlogger = getHtmlParser(blogID).parseBlogger();
                        results.add(dstBlogger);
                    }
                }
                //保存最新的博主信息到数据库
                mDBHelper.insertBloggers(results);

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
                Thread.sleep(10000);
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
     * 从网络加载指定博主的所有博客文章的代码执行段
     * @param subscriber
     * @param blogger
     * @param startIndex
     * @param requestLen
     * @return
     */
    private Runnable getArticlesFromNet(final Subscriber<? super List<BlogArticle>> subscriber,
                                        Blogger blogger, int startIndex, int requestLen){
        return ()->{
            try {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                //从网络更新博客文章信息
                List<BlogArticle> articles = getHtmlParser(blogger.blogID).parseArticles();
                if(articles == null)
                    articles = new ArrayList<>();
                for(BlogArticle article: articles){
                    article.blogger = blogger;
                }

                //删除原数据库中所有该博主对应的文章
                mDBHelper.deleteArticles(blogger);
                //保存最新的博客文章信息到数据库
                mDBHelper.insertArticles(articles);
                //从数据库重新查找博客文章
                articles = mDBHelper.findArticlesByPaging(blogger, startIndex, requestLen);

                subscriber.onNext(articles);
                subscriber.onCompleted();
            } catch(Exception e){
                subscriber.onError(e);
            }
        };
    }

    /**
     * 从数据库加载指定博主的博客文章的代码执行段
     * @param subscriber
     * @param blogger
     * @param startIndex
     * @param requestLen
     * @return
     */
    private Runnable getArticlesFromDB(final Subscriber<? super List<BlogArticle>> subscriber,
                                       Blogger blogger, int startIndex, int requestLen){
        return ()->{
            try {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                //从数据库查找博客文章信息
                List<BlogArticle> articles = mDBHelper.findArticlesByPaging(blogger, startIndex, requestLen);

                subscriber.onNext(articles);
                subscriber.onCompleted();
            } catch(Exception e){
                subscriber.onError(e);
            }
        };
    }

    /**
     * 从网络加载所有博主信息的代码执行段
     * @param subscriber
     * @param blogID
     * @return
     */
    private Runnable getLoadBloggerFromNet(final Subscriber<? super Blogger> subscriber, String blogID){
        return ()->{
            try {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                //从网络更新博主信息
                Blogger blogger = getHtmlParser(blogID).parseBlogger();
                //保存最新的博主信息到数据库
                if(blogger != null){
                    mDBHelper.insertBlogger(blogger);
                }

                subscriber.onNext(blogger);
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
                subscriber.onNext(blogger);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        };
    }
}
