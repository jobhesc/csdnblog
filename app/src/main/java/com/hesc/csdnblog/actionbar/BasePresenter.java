package com.hesc.csdnblog.actionbar;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by hesc on 15/4/24.
 */
abstract class BasePresenter {
    private Activity mActivity;
    private IActionBar mActionBar;

    public BasePresenter(Activity activity, IActionBar actionBar){
        mActivity = activity;
        mActionBar = actionBar;
    }

    public Activity getActivity(){
        return mActivity;
    }

    public IActionBar getActionBar(){
        return mActionBar;
    }

    /**
     * 以嵌入系统ActionBar的方式显示
     * @param contentView
     */
    public void showAsEmbedded(View contentView)  {
        showAsEmbedded(contentView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 隐藏系统的ActionBar，使用独立自定义的ActionBar
     * @param contentView
     */
    public void showAsIndependent(View contentView) {
        showAsIndependent(contentView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 以嵌入系统ActionBar的方式显示
     * @param contentView
     * @param  layoutParams
     */
    public abstract void showAsEmbedded(View contentView, ViewGroup.LayoutParams layoutParams);
    /**
     * 隐藏系统的ActionBar，使用独立自定义的ActionBar
     * @param contentView
     * @param  layoutParams
     */
    public abstract void showAsIndependent(View contentView, ViewGroup.LayoutParams layoutParams);
}
