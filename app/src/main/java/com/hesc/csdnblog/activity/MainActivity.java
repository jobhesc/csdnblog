package com.hesc.csdnblog.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.actionbar.ActionBarFacade;
import com.hesc.csdnblog.actionbar.ActionBarFactory;
import com.hesc.csdnblog.actionbar.IActionBar;
import com.hesc.csdnblog.adapter.BloggerListAdapter;
import com.hesc.csdnblog.base.BaseActivity;
import com.hesc.csdnblog.data.Blogger;
import com.hesc.csdnblog.data.DataloadCallback;
import com.hesc.csdnblog.data.InitLoader;
import com.hesc.csdnblog.dialog.MessageBox;
import com.hesc.csdnblog.view.RefreshableView;

import org.w3c.dom.Text;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    @InjectView(R.id.main_blogger)
    RefreshableView mListView;

    private BloggerListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBarFacade().getActionBar().setLeftButtonVisible(false)
                .setRightButton1ImageResource(R.drawable.actionbar_search)
                .setOnRightButton1ClickListener(mRightClickListener);
        getActionBarFacade().getActionBar().setMenuLayout(R.menu.menu_main);
        getActionBarFacade().getActionBar().setMenuItemClickListener(mMenuItemClickListener);
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
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, BlogActivity.class);
            intent.putExtra("blogger", (Blogger) mAdapter.getItem(position));
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        InitLoader.getInstance().cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        MessageBox.showConfirm(this, "您确定要退出吗？", v->{
            super.onBackPressed();
            return true;
        });
    }

    /**
     * actionbar右边按钮点击事件回调
     */
    private View.OnClickListener mRightClickListener = v->{
        startActivity(new Intent(this, SearchActivity.class));
    };

    /**
     * 菜单点击事件回调
     */
    private MenuItem.OnMenuItemClickListener mMenuItemClickListener = item->{
        switch(item.getItemId()){
            case R.id.menu_add_blogger:
                View contentView = getLayoutInflater().inflate(R.layout.dialog_confirm_add_blogger, null);
                EditText blogIdView = (EditText) contentView.findViewById(R.id.et_blog_id);
                MessageBox.showConfirm(this, false, contentView, null, v->{
                    String blogID = blogIdView.getText().toString();
                    if(TextUtils.isEmpty(blogID)){
                        blogIdView.setError("博客ID不能为空！");
                        return false;
                    }
                    //从网络加载指定博客ID的博客信息
                    mAdapter.loadFromNet(blogID);
                    return true;
                });
                return true;
        }
        return false;
    };
}
