package com.hesc.csdnblog.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.adapter.BloggerListAdapter;
import com.hesc.csdnblog.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends BaseActivity{
    @InjectView(R.id.main_blogger)
    ListView mListView;

    private BloggerListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ActionBar只有标题栏
        getActionBarFacade().setOnlyTitleActionBar();
        ButterKnife.inject(this);

        mAdapter = new BloggerListAdapter(this);
        mListView.setAdapter(mAdapter);
        mAdapter.loadBlogger("Luoshengyang");
        mAdapter.loadBlogger("sinyu890807");
        mAdapter.loadBlogger("q199109106q");
        mAdapter.loadBlogger("lmj623565791");
        mAdapter.loadBlogger("u013357243");
        mAdapter.loadBlogger("huangyanan1989");
    }
}
