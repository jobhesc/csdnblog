package com.hesc.csdnblog.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.adapter.BloggerListAdapter;
import com.hesc.csdnblog.adapter.DataloadCallback;
import com.hesc.csdnblog.base.BaseActivity;
import com.hesc.csdnblog.view.RefreshableView;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends BaseActivity implements DataloadCallback {
    @InjectView(R.id.main_blogger)
    RefreshableView mListView;

    private BloggerListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ActionBar只有标题栏
        getActionBarFacade().setOnlyTitleActionBar();
        ButterKnife.inject(this);

        mAdapter = new BloggerListAdapter(this);
        mAdapter.loadAllBloggersFromDB(null);
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(new RefreshableView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.loadAllBloggersFromNet(MainActivity.this);
            }

            @Override
            public void onLoadMore() {
            }
        });

//        mAdapter.loadBloggerFromNet("Luoshengyang");
//        mAdapter.loadBloggerFromNet("sinyu890807");
//        mAdapter.loadBloggerFromNet("q199109106q");
//        mAdapter.loadBloggerFromNet("lmj623565791");
//        mAdapter.loadBloggerFromNet("u013357243");
//        mAdapter.loadBloggerFromNet("huangyanan1989");

//        mListView.setOnItemClickListener();
    }

    @Override
    public void dataLoaded() {
        mListView.stopRefresh(true);
    }

    @Override
    public void dataLoadFail() {
        mListView.stopRefresh(false);

    }
}
