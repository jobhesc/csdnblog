package com.hesc.csdnblog.data;

import android.content.Context;

import com.hesc.csdnblog.application.MyApplication;

import java.util.ArrayList;
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

    interface ProviderAction<T>{
        T call() throws Exception;
    }

    interface ProviderFail<T>{
        T call();
    }

    private static final String LOG_TAG=BlogProvider.class.getName();

    private Context mContext = null;
    private BlogDBHelper mDBHelper = null;
    private Executor dbExecutor;
    private Executor httpExecutor;
    private volatile static BlogProvider instance;

    private BlogProvider(Context context){
        mContext = context;
        mDBHelper = new BlogDBHelper(mContext);
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

    private <T> Observable<T> createAsyncObservable(Executor executor, ProviderAction<T> action){
        return createAsyncObservable(executor, action, null);
    }

    /**
     * 创建异步的observale
     * @param executor
     * @param action
     * @param fail
     * @param <T>
     * @return
     */
    private <T> Observable<T> createAsyncObservable(Executor executor, ProviderAction<T> action,
                                                    ProviderFail<T> fail){
        return Observable.create(subscriber -> {
            Runnable runnable = getRunnable(subscriber, action, fail);
            FutureTask<Void> task = new FutureTask<Void>(runnable, null);
            subscriber.add(Subscriptions.from(task));
            executor.execute(task);
        });
    }

    /**
     * 获取代码执行段
     * @param subscriber
     * @param action
     * @param fail
     * @param <T>
     * @return
     */
    private <T> Runnable getRunnable(final Subscriber<? super T> subscriber,
                                     ProviderAction<T> action, ProviderFail<T> fail){
        return ()->{
            try {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                T result = null;
                if(action != null)
                    result = action.call();
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception e) {
                if(fail != null) {
                    T result = fail.call();
                    if(result != null) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }
                }
                subscriber.onError(e);
            }
        };
    }

    /**
     * 从网络分页装载博主对应的博客文章
     * @param blogger
     * @param startIndex
     * @param requestLen
     * @return
     */
    public Observable<List<BlogArticle>> loadArticlesFromNet(Blogger blogger, int startIndex, int requestLen){
        return createAsyncObservable(httpExecutor, ()->{
            //从网络更新博客文章信息
            List<BlogArticle> articles = BlogHtmlParser.parseArticles(blogger.blogID);
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
            return articles;
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
        return createAsyncObservable(dbExecutor,
                () -> mDBHelper.findArticlesByPaging(blogger, startIndex, requestLen));
    }

    /**
     * 从网络装载博客ID对应的博主信息
     * @param blogID
     * @return
     */
    public Observable<Blogger> loadBloggerFromNet(String blogID){
        return createAsyncObservable(httpExecutor, ()->{
            //从网络更新博主信息
            Blogger blogger = BlogHtmlParser.parseBlogger(blogID);
            //保存最新的博主信息到数据库
            if(blogger != null){
                mDBHelper.insertBlogger(blogger);
            }
            return blogger;
        });
    }

    /**
     * 从数据库装载博客ID对应的博主信息
     * @return
     */
    public Observable<Blogger> loadBloggerFromDB(String blogID){
        return createAsyncObservable(dbExecutor, () -> mDBHelper.findBlogger(blogID));
    }

    /**
     * 从网络装载博主信息
     * @param blogIDs
     * @return
     */
    public Observable<List<Blogger>> loadBloggersFromNet(List<String> blogIDs){
        return createAsyncObservable(httpExecutor, () -> {
            List<Blogger> results = new ArrayList<>();
            if (blogIDs != null && blogIDs.size() > 0) {
                Blogger dstBlogger = null;
                for (String blogID : blogIDs) {
                    //从网络更新博主信息
                    dstBlogger = BlogHtmlParser.parseBlogger(blogID);
                    results.add(dstBlogger);
                }
            }
            //保存最新的博主信息到数据库
            mDBHelper.insertBloggers(results);
            return results;
        });
    }

    /**
     * 从数据库装载所有博主信息
     * @return
     */
    public Observable<List<Blogger>> loadAllBloggerFromDB(){
        return createAsyncObservable(dbExecutor, () -> mDBHelper.findAllBloggers());
    }

    /**
     * 装载博客文章内容
     * @param articleUrl
     * @return
     */
    public Observable<String> loadArticleContent(String articleUrl){
        return createAsyncObservable(httpExecutor,
                ()-> {
                    //从网络上加载博客文章内容，并进行裁剪处理
                    String articleContent = BlogHtmlParser.clipArticle(articleUrl);
                    //保存到缓存中
                    ArticleCache.getInstance().put(articleUrl, articleContent);
                    return articleContent;
                },
                //从网络加载失败后，就从缓存中加载
                ()->ArticleCache.getInstance().get(articleUrl)
        );
    }
}
