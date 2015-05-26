package com.hesc.csdnblog.activity;

import android.content.Intent;
import android.os.Bundle;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.adapter.ArticleListAdapter;
import com.hesc.csdnblog.base.BaseActivity;
import com.hesc.csdnblog.data.BlogArticle;
import com.hesc.csdnblog.data.Blogger;
import com.hesc.csdnblog.data.DataloadCallback;
import com.hesc.csdnblog.view.RefreshableView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BlogActivity extends BaseActivity implements DataloadCallback {
    @InjectView(R.id.blog_article_list)
    RefreshableView mListView;
    private ArticleListAdapter mAdapter;
    private boolean isFirstLoading = true;  //是否第一次装载数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        getActionBarFacade().setBackActionBar();
        ButterKnife.inject(this);
        if(savedInstanceState != null)
            isFirstLoading = savedInstanceState.getBoolean("isFirstLoading");
        else
            isFirstLoading = true;

        Blogger blogger = (Blogger)getIntent().getSerializableExtra("blogger");
        setTitle(blogger.blogName);
        mAdapter = new ArticleListAdapter(this, blogger);
        mListView.setRefreshTAG(blogger.blogID);
        mListView.setAdapter(mAdapter);
        mAdapter.setDataLoadCallback(this);
        setListener();
        //装载数据
        mAdapter.loadNext();
    }

    private void setListener(){
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

        mListView.setOnItemClickListener((parent, view, position, id) ->{
            Intent intent = new Intent(BlogActivity.this, ArticleActivity.class);
            intent.putExtra("article", (BlogArticle)mAdapter.getItem(position));
            startActivity(intent);
        });
    }

    /**
     * 第一次进入时从网络装载数据
     */
    private void loadDataFromNetOnFirst(){
        if(isFirstLoading) {
            mListView.startRefresh();
            isFirstLoading = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFirstLoading", isFirstLoading);
    }

    @Override
    public void dataLoaded() {
        //刷新数据成功
        if (mListView.isRefreshing())
            mListView.stopRefresh(true);
        //加载更多成功
        if (mListView.isLoadingMore())
            mListView.stopLoadMore(true);

        //如果数据已经加载完成，则隐藏加载更多
        mListView.setFooterViewEnabled(!mAdapter.isEnd());
        //第一次进入时从网络装载数据
        loadDataFromNetOnFirst();
    }

    @Override
    public void dataLoadFail() {
        //刷新数据失败
        if (mListView.isRefreshing())
            mListView.stopRefresh(false);
        //加载更多失败
        if (mListView.isLoadingMore())
            mListView.stopLoadMore(false);
        //第一次进入时从网络装载数据
        loadDataFromNetOnFirst();
    }
}
