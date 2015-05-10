package com.hesc.csdnblog.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.base.BaseActivity;

public class BlogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        //ActionBar只有标题栏
        getActionBarFacade().setBackActionBar();
    }

}
