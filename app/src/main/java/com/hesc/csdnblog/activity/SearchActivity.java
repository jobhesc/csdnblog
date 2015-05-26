package com.hesc.csdnblog.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.actionbar.ActionBarFactory;
import com.hesc.csdnblog.actionbar.IActionBar;
import com.hesc.csdnblog.adapter.BloggerListAdapter;
import com.hesc.csdnblog.base.BaseActivity;
import com.hesc.csdnblog.data.Blogger;
import com.hesc.csdnblog.data.DataloadCallback;
import com.hesc.csdnblog.data.InitLoader;
import com.hesc.csdnblog.dialog.MessageBox;
import com.hesc.csdnblog.view.RefreshableView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchActivity extends BaseActivity {
    @InjectView(R.id.main_blogger)
    RefreshableView mListView;

    private BloggerListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
