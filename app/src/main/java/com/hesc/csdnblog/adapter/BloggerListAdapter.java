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
     * 装载指定博客ID的博主信息
     * @param blogID
     */
    public void loadBlogger(String blogID){
        mProvider.loadBlogger(blogID).observeOn(AndroidSchedulers.mainThread()).subscribe(
                r->{
                    if(r!=null) {
                        mBloggers.add(r);
                        sortBloggers();
                        notifyDataSetChanged();
                    }
                },
                e->{
                    showError(e);
                }
        );
    }

    /**
     * 装载所有博主信息数据
     */
    public void loadAllBloggers(){
        mProvider.loadAllBloggers().observeOn(AndroidSchedulers.mainThread()).subscribe(
                r->{
                    mBloggers.clear();
                    if(r!=null) {
                        mBloggers.addAll(r);
                        sortBloggers();
                        notifyDataSetChanged();
                    }
                },
                e->{
                    showError(e);
                }
        );
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
