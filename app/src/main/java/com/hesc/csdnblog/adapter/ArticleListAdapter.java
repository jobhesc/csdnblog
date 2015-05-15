package com.hesc.csdnblog.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.base.BaseActivity;
import com.hesc.csdnblog.base.BaseListAdapter;
import com.hesc.csdnblog.data.BlogArticle;
import com.hesc.csdnblog.data.BlogProvider;
import com.hesc.csdnblog.data.Blogger;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by hesc on 15/5/5.
 */
public class ArticleListAdapter extends BaseListAdapter<BlogArticle> {
    private static final int PAGE_SIZE = 20;  //每一页的博客文章数量

    private BlogProvider mProvider;
    private Blogger mBlogger;
    private int mLastLoadedIndex = 0;   //上一次装载的博客最大索引
    private boolean mIsEnd = false;    //是否已经为数据最末尾

    public ArticleListAdapter(BaseActivity activity, Blogger blogger){
        super(activity);
        mBlogger = blogger;
        mProvider = BlogProvider.getInstance();
        reset();
    }

    public Blogger getBlogger(){
        return mBlogger;
    }

    /**
     * 是否数据装载完成
     * @return
     */
    public boolean isEnd(){
        return mIsEnd;
    }

    /**
     * 装载下一页数据
     */
    public void loadNext(){
        safeSubscription(mProvider.loadArticlesFromDB(mBlogger, mLastLoadedIndex + 1, PAGE_SIZE)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        r -> {
                            if (r != null) {
                                mDatas.addAll(r);
                                setState(r.size());
                                notifyDataSetChanged();
                            }
                            notifyDataLoaded();
                        },
                        e -> {
                            showError(e);
                            notifyDataLoadFail();
                        }
                ));
    }

    /**
     * 重置数据
     */
    public void reset(){
        mLastLoadedIndex = -1;
        mIsEnd = false;
        mDatas.clear();
    }

    private void setState(int size){
        mLastLoadedIndex += size;
        //获取到的数据小于请求页数，表示已经装载完成
        if (size < PAGE_SIZE)
            mIsEnd = true;
    }

    /**
     * 从网络装载指博客文章信息
     */
    public void loadFromNet(){
        safeSubscription(mProvider.loadArticlesFromNet(mBlogger, 0, PAGE_SIZE)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        r -> {
                            if (r != null) {
                                reset();
                                mDatas.addAll(r);
                                setState(r.size());
                                notifyDataSetChanged();
                            }
                            notifyDataLoaded();
                        },
                        e -> {
                            showError(e);
                            notifyDataLoadFail();
                        }
                ));
    }

    @Override
    protected void bindView(View convertView, int position) {
        BlogArticle article = (BlogArticle)getItem(position);

        ImageView iconView = findView(convertView, R.id.listview_article_icon);
        TextView titleView = findView(convertView, R.id.listview_article_title);
        TextView summayView = findView(convertView, R.id.listview_article_summay);
        TextView updateOnView = findView(convertView, R.id.listview_article_updateOn);
        TextView readStateView = findView(convertView, R.id.listview_article_readState);
        TextView commentStateView = findView(convertView, R.id.listview_article_commentState);

        iconView.setImageDrawable(getArticleCategoryDrawable(article));
        titleView.setText(article.title);
        summayView.setText(article.summary);
        updateOnView.setText(article.updateOn);
        readStateView.setText(article.readCount);
        commentStateView.setText(article.commentCount);
    }

    /**
     * 获取文章的类型图标
     * @param article
     * @return
     */
    public Drawable getArticleCategoryDrawable(BlogArticle article){
        switch (article.category){
            case BlogArticle.ORIGINAL:
                return mActivity.getResources().getDrawable(R.drawable.icon_original);
            case BlogArticle.REPOST:
                return mActivity.getResources().getDrawable(R.drawable.icon_repost);
            case BlogArticle.TRANSLATED:
                return mActivity.getResources().getDrawable(R.drawable.icon_translated);
            default:
                return null;
        }
    }

    @Override
    protected View createView(int position) {
        return mInflater.inflate(R.layout.listview_articles, null);
    }
}
