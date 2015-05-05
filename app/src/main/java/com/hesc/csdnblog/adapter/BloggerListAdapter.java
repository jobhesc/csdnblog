package com.hesc.csdnblog.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.base.BaseActivity;
import com.hesc.csdnblog.base.BaseListAdapter;
import com.hesc.csdnblog.data.BlogProvider;
import com.hesc.csdnblog.data.Blogger;
import com.hesc.csdnblog.view.CircleMaskImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
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
     * 装载指定博客ID的博主信息
     * @param blogID
     */
    public void loadBlogger(String blogID){
        mProvider.loadBlogger(blogID).observeOn(AndroidSchedulers.mainThread()).subscribe(
                r->{
                    if(r!=null)
                        mBloggers.add(r);
                    notifyDataSetChanged();
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
                    if(r!=null)
                        mBloggers.addAll(r);
                    notifyDataSetChanged();
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
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.titleView.setText(blogger.blogName);
        holder.summayView.setText(blogger.blogDesc);
//        ImageLoader.getInstance().loadImage(blogger.iconUrl, new SimpleImageLoadingListener(){
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                holder.imageView.setImageBitmap(loadedImage);
//            }
//        });
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).build();
        ImageLoader.getInstance().displayImage(blogger.iconUrl, holder.imageView,options);
    }

    @Override
    protected View createView(int position) {
        View convertView = mInflater.inflate(R.layout.listview_blogger, null);
        ViewHolder holder = new ViewHolder();
        holder.imageView = (CircleMaskImageView) convertView.findViewById(R.id.listview_blogger_image);
        holder.titleView = (TextView) convertView.findViewById(R.id.listview_blogger_title);
        holder.summayView = (TextView) convertView.findViewById(R.id.listview_blogger_summay);
        convertView.setTag(holder);
        return convertView;
    }

    private class ViewHolder{
        CircleMaskImageView imageView;
        TextView titleView;
        TextView summayView;
    }
}
