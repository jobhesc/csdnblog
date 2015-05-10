package com.hesc.csdnblog.adapter;

import android.view.View;
import android.widget.TextView;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.base.BaseActivity;
import com.hesc.csdnblog.base.BaseListAdapter;
import com.hesc.csdnblog.data.BlogProvider;
import com.hesc.csdnblog.data.Blogger;
import com.hesc.csdnblog.view.CircleMaskImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by hesc on 15/5/5.
 */
public class BloggerListAdapter extends BaseListAdapter {
    private BlogProvider mProvider;
    private List<Blogger> mBloggers = new ArrayList<>();

    public BloggerListAdapter(BaseActivity activity){
        super(activity);
        mProvider = new BlogProvider(activity);

    }

    /**
     * 对博主信息按照博主ID进行排序
     */
    private void sortBloggers(){
        Collections.sort(mBloggers, (lhs, rhs) -> Long.valueOf(lhs.id).compareTo(rhs.id));
    }

    /**
     * 从网络装载指定博客ID的博主信息
     * @param blogID
     */
    public void loadBloggerFromNet(String blogID,DataloadCallback callback){
        safeSubscription(mProvider.loadBloggerFromNet(blogID).observeOn(AndroidSchedulers.mainThread()).subscribe(
                r -> {
                    if (r != null) {
                        mBloggers.add(r);
                        sortBloggers();
                        notifyDataSetChanged();
                    }
                    if(callback != null)
                        callback.dataLoaded();
                },
                e -> {
                    showError(e);
                    if(callback != null)
                        callback.dataLoadFail();
                }
        ));
    }

    /**
     * 从数据库装载指定博客ID的博主信息
     * @param blogID
     */
    public void loadBloggerFromDB(String blogID,DataloadCallback callback){
        safeSubscription(mProvider.loadBloggerFromDB(blogID).observeOn(AndroidSchedulers.mainThread()).subscribe(
                r -> {
                    if (r != null) {
                        mBloggers.add(r);
                        sortBloggers();
                        notifyDataSetChanged();
                    }
                    if(callback != null)
                        callback.dataLoaded();
                },
                e -> {
                    showError(e);
                    if(callback != null)
                        callback.dataLoadFail();
                }
        ));
    }

    /**
     * 从网络装载所有博主信息数据
     */
    public void loadAllBloggersFromNet(DataloadCallback callback){
        safeSubscription(mProvider.loadBloggersFromNet(mBloggers).observeOn(AndroidSchedulers.mainThread()).subscribe(
                r -> {
                    mBloggers.clear();
                    if (r != null) {
                        mBloggers.addAll(r);
                        sortBloggers();
                        notifyDataSetChanged();
                    }
                    if(callback != null)
                        callback.dataLoaded();
                },
                e -> {
                    showError(e);
                    if(callback != null)
                        callback.dataLoadFail();
                }
        ));
    }

    /**
     * 从数据库装载所有博主信息数据
     */
    public void loadAllBloggersFromDB(DataloadCallback callback){
        safeSubscription(mProvider.loadAllBloggerFromDB().observeOn(AndroidSchedulers.mainThread()).subscribe(
                r -> {
                    mBloggers.clear();
                    if (r != null) {
                        mBloggers.addAll(r);
                        sortBloggers();
                        notifyDataSetChanged();
                    }
                    if(callback != null)
                        callback.dataLoaded();
                },
                e -> {
                    showError(e);
                    if(callback != null)
                        callback.dataLoadFail();
                }
        ));
    }

    @Override
    public int getCount() {
        return mBloggers.size();
    }

    @Override
    protected void bindView(View convertView, int position) {
        Blogger blogger = mBloggers.get(position);

        CircleMaskImageView imageView = findView(convertView, R.id.listview_blogger_image);
        TextView titleView = findView(convertView, R.id.listview_blogger_title);
        TextView summayView = findView(convertView, R.id.listview_blogger_summay);

        titleView.setText(blogger.blogName);
        summayView.setText(blogger.blogDesc);
        //装载图片并缓存起来
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).build();
        ImageLoader.getInstance().displayImage(blogger.iconUrl, imageView,options);
    }

    @Override
    protected View createView(int position) {
        return mInflater.inflate(R.layout.listview_blogger, null);
    }
}
