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

import java.util.Collections;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by hesc on 15/5/5.
 */
public class BloggerListAdapter extends BaseListAdapter<Blogger> {
    private BlogProvider mProvider;

    public BloggerListAdapter(BaseActivity activity){
        super(activity);
        mProvider = BlogProvider.getInstance();

    }

    /**
     * 对博主信息按照博主ID进行排序
     */
    private void sortBloggers(){
        Collections.sort(mDatas, (lhs, rhs) -> lhs.blogID.compareTo(rhs.blogID));
    }

    /**
     * 从网络装载指定博客ID的博主信息
     * @param blogID
     */
    public void loadFromNet(String blogID){
        mActivity.showWaitingProgress();
        safeSubscription(mProvider.loadBloggerFromNet(blogID).observeOn(AndroidSchedulers.mainThread()).subscribe(
                r -> {
                    mActivity.hideWaitingProgress();
                    if (r != null) {
                        mDatas.add(r);
                        sortBloggers();
                        notifyDataSetChanged();
                    }
                    notifyDataLoaded();
                },
                e -> {
                    mActivity.hideWaitingProgress();
                    showError(e);
                    notifyDataLoadFail();
                }
        ));
    }

    /**
     * 从数据库装载指定博客ID的博主信息
     * @param blogID
     */
    public void loadFromDB(String blogID){
        mActivity.showWaitingProgress();
        safeSubscription(mProvider.loadBloggerFromDB(blogID).observeOn(AndroidSchedulers.mainThread()).subscribe(
                r -> {
                    mActivity.hideWaitingProgress();
                    if (r != null) {
                        mDatas.add(r);
                        sortBloggers();
                        notifyDataSetChanged();
                    }
                    notifyDataLoaded();
                },
                e -> {
                    mActivity.hideWaitingProgress();
                    showError(e);
                    notifyDataLoadFail();
                }
        ));
    }

    /**
     * 从数据库装载所有博主信息数据
     */
    public void loadAllFromDB(){
        mActivity.showWaitingProgress();
        safeSubscription(mProvider.loadAllBloggerFromDB().observeOn(AndroidSchedulers.mainThread()).subscribe(
                r -> {
                    mActivity.hideWaitingProgress();
                    mDatas.clear();
                    if (r != null) {
                        mDatas.addAll(r);
                        sortBloggers();
                        notifyDataSetChanged();
                    }
                    notifyDataLoaded();
                },
                e -> {
                    mActivity.hideWaitingProgress();
                    showError(e);
                    notifyDataLoadFail();
                }
        ));
    }

    @Override
    protected void bindView(View convertView, int position) {
        Blogger blogger = (Blogger)getItem(position);

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
