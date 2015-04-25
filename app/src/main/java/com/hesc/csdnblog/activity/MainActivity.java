package com.hesc.csdnblog.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.actionbar.IActionBar;
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
