package com.hesc.csdnblog.data;

import android.content.Context;

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
    private Context mContext = null;
    private BlogDBHelper mDBHelper = null;
    private Executor httpExecutor;
    private Executor dbExecutor;

    public BlogProvider(Context context){
        mContext = context;
        mDBHelper = new BlogDBHelper(mContext);

        httpExecutor = Executors.newCachedThreadPool();
        dbExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * 装载所有的博主信息
     * @return
     */
    public Observable<List<Blogger>> loadAllBloggers(){
        return Observable.create(subscriber -> {
            //从数据库装载所有的博主信息
            Runnable runnable = getLoadBloggerRunnale(subscriber);
            FutureTask<Void> task = new FutureTask<Void>(runnable, null);
            subscriber.add(Subscriptions.from(task));
            dbExecutor.execute(task);
        });
    }

    private Runnable getLoadBloggerRunnale(final Subscriber<? super List<Blogger>> subscriber){
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
}
