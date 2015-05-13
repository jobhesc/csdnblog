package com.hesc.csdnblog.activity;

import android.os.Bundle;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.adapter.ArticleListAdapter;
import com.hesc.csdnblog.base.BaseActivity;
import com.hesc.csdnblog.data.Blogger;
import com.hesc.csdnblog.data.DataloadCallback;
import com.hesc.csdnblog.view.RefreshableView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BlogActivity extends BaseActivity implements DataloadCallback {
    @InjectView(R.id.blog_article)
    RefreshableView mListView;
    private ArticleListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        //ActionBar只有标题栏
        getActionBarFacade().setBackActionBar();
        ButterKnife.inject(this);

        Blogger blogger = (Blogger)getIntent().getSerializableExtra("blogger");
        mAdapter = new ArticleListAdapter(this, blogger);
        mListView.setAdapter(mAdapter);
        //开始刷新数据
        mListView.startRefresh();
        mListView.setOnRefreshListener(new RefreshableView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //从网络更新数据
                mAdapter.loadFromNet();
            }

            @Override
            public void onLoadMore() {
                //从数据库更新数据
                mAdapter.loadNext();
            }
        });
    }

    @Override
    public void dataLoaded() {
        //刷新数据成功
        if(mListView.isRefreshing())
            mListView.stopRefresh(true);
        //加载更多成功
        if(mListView.isLoadingMore())
            mListView.stopLoadMore(true);

        //如果数据已经加载完成，则隐藏加载更多
        mListView.setFooterViewEnabled(!mAdapter.isEnd());
    }

    @Override
    public void dataLoadFail() {
        //刷新数据失败
        if(mListView.isRefreshing())
            mListView.stopRefresh(false);
        //加载更多失败
        if(mListView.isLoadingMore())
            mListView.stopLoadMore(false);
    }
}
