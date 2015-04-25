package com.hesc.csdnblog.activity;

import android.os.Bundle;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.base.BaseActivity;


public class MainActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ActionBar只有标题栏
        getActionBarFacade().setOnlyTitleActionBar();
    }
}
