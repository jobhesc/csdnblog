package com.hesc.csdnblog.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.actionbar.ActionBarFactory;
import com.hesc.csdnblog.actionbar.IActionBar;
import com.hesc.csdnblog.adapter.BloggerListAdapter;
import com.hesc.csdnblog.base.BaseActivity;
import com.hesc.csdnblog.data.Blogger;
import com.hesc.csdnblog.data.DataloadCallback;
import com.hesc.csdnblog.data.InitLoader;
import com.hesc.csdnblog.view.RefreshableView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity implements MenuItem.OnMenuItemClickListener {
    @InjectView(R.id.main_blogger)
    RefreshableView mListView;

    private BloggerListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ActionBar只有标题栏
        getActionBarFacade().setBackExtActionBar();
        getActionBarFacade().getActionBar().setMenuLayout(R.menu.menu_main);
        getActionBarFacade().getActionBar().setMenuItemClickListener(this);
        ButterKnife.inject(this);

        mAdapter = new BloggerListAdapter(this);
        mListView.setAdapter(mAdapter);
        setListener();
        loadData();
    }

    @Override
    public IActionBar createActionBar() {
        return ActionBarFactory.createMenuActionBar(this);
    }

    private void loadData(){

        if(InitLoader.getInstance().isInitLoaded()) {  //已经装载过数据
            //装载数据库中所有的博主信息
            mAdapter.loadAllFromDB();
        } else {
            //进行初始化数据操作
            showWaitingProgress();
            InitLoader.getInstance().execute(new DataloadCallback() {
                @Override
                public void dataLoaded() {
                    mAdapter.loadAllFromDB();
                    hideWaitingProgress();
                }

                @Override
                public void dataLoadFail() {
                    mAdapter.loadAllFromDB();
                    hideWaitingProgress();
                }
            });
        }
    }

    private void setListener(){
        mListView.setOnItemClickListener((parent, view, position, id) ->{
            Intent intent = new Intent(MainActivity.this, BlogActivity.class);
            intent.putExtra("blogger", (Blogger)mAdapter.getItem(position));
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        InitLoader.getInstance().cancel();
        super.onDestroy();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_settings:
                Toast.makeText(this, "测试设置功能", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }
}
