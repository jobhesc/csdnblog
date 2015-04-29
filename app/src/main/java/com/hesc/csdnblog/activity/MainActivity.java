package com.hesc.csdnblog.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.base.BaseActivity;
import com.hesc.csdnblog.view.RefreshableView;


public class MainActivity extends BaseActivity{
    private RefreshableView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ActionBar只有标题栏
        getActionBarFacade().setOnlyTitleActionBar();


        mListView = (RefreshableView)findViewById(R.id.listview);
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                new String[]{"张三", "李四", "王五", "刘备", "张飞", "关羽", "诸葛亮", "曹操",
                        "司马懿", "夏侯惇", "荀彧", "夏侯霸", "张郃","孙权", "周瑜"}));
        mListView.setOnRefreshListener(new RefreshableView.OnRefreshListener() {
            @Override
            public boolean onRefresh() {
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean onLoadMore() {
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("demo", "test");
            }
        });
    }
}
