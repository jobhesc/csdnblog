package com.hesc.csdnblog.actionbar;

import android.app.Activity;
import android.content.Context;

import com.hesc.csdnblog.R;

/**
 * Created by hesc on 15/4/22.
 */
public class ActionBarFacade {
    private Activity mActivity;
    private IActionBar mActionBar;
    private ActionBarShowPolicy mShowPolicy;

    public ActionBarFacade(Activity activity, IActionBar actionbar){
        mActivity = activity;
        mActionBar = actionbar;
        mShowPolicy = new ActionBarShowPolicy(activity, actionbar);
    }

    public ActionBarShowPolicy getShowPolicy(){
        return mShowPolicy;
    }

    public IActionBar getActionBar(){
        return mActionBar;
    }

    /**
     * 设置只有一个标题栏的ActionBar
     */
    public void setOnlyTitleActionBar(){
        mActionBar.setLeftButtonVisible(false)
                .setTitleViewVisible(true)
                .setRightButtonVisible(false);
    }

    /**
     * 设置有返回按钮+标题栏的ActionBar
     */
    public void setBackActionBar(){
        innerBuildCommonActionBar(false, R.drawable.actionbar_back);
    }

    /**
     * 设置有返回按钮+标题栏+右边按钮的ActionBar
     */
    public void setBackExtActionBar(){
        innerBuildCommonActionBar(true, R.drawable.actionbar_back);
    }

    /**
     * 构建有关闭按钮+标题栏的ActionBar
     */
    public void setCloseActionBar(){
        innerBuildCommonActionBar(false, R.drawable.actionbar_close);
    }

    /**
     * 构建有关闭按钮+标题栏+右边按钮的ActionBar
     */
    public void setCloseExtActionBar(){
        innerBuildCommonActionBar(true, R.drawable.actionbar_close);
    }

    private void innerBuildCommonActionBar(boolean rightButtonVisible,
                                                 int leftButtonResID){
        mActionBar.setLeftButtonVisible(true)
                .setTitleViewVisible(true)
                .setRightButtonVisible(rightButtonVisible);
        //设置左边按钮图片
        mActionBar.setLeftButtonImageResource(leftButtonResID);
        //点击左边按钮返回
        mActionBar.setOnLeftButtonClickListener(v -> finishActivity());
    }

    /**
     * 关闭结束Activity
     */
    private void finishActivity(){
        if(!mActivity.isTaskRoot())
            mActivity.finish();
    }
}
