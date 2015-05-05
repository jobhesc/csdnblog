package com.hesc.csdnblog.base;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * Created by hesc on 15/5/5.
 */
public abstract class BaseListAdapter extends BaseAdapter {
    protected  static final String LOG_TAG=BaseListAdapter.class.getName();
    protected BaseActivity mActivity;
    protected LayoutInflater mInflater;

    public BaseListAdapter(BaseActivity activity){
        mActivity = activity;
        mInflater = mActivity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = createView(position);
        }
        bindView(convertView, position);
        return convertView;
    }

    protected void showError(Throwable e){
        Log.e(LOG_TAG, e.getMessage(), e);
        mActivity.showToast(e.getMessage());
    }

    /**
     * 绑定视图数据
     * @param convertView
     * @param position
     */
    protected abstract void bindView(View convertView, int position);

    /**
     * 创建列表每一项的视图
     * @param position
     * @return
     */
    protected abstract View createView(int position);
}
