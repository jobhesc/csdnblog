package com.hesc.csdnblog.actionbar;

import android.app.Activity;

import com.hesc.csdnblog.R;

/**
 * Created by hesc on 15/4/22.
 */
public class ActionBarFacade {
    /**
     * 显示所有右边按钮
     */
    public static final int RIGHTBUTTON_ALL = 1;
    /**
     * 显示右边第一个按钮
     */
    public static final int RIGHTBUTTON_FIRST = 2;
    /**
     * 显示右边第二个按钮
     */
    public static final int RIGHTBUTTON_SECOND = 3;
    /**
     * 不显示右边按钮
     */
    public static final int RIGHTBUTTON_NONE = 4;

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
                .setRightButton1Visible(false)
                .setRightButton2Visible(false);
    }

    /**
     * 设置有返回按钮+标题栏的ActionBar
     */
    public void setBackActionBar(){
        innerBuildCommonActionBar(RIGHTBUTTON_NONE, R.drawable.actionbar_back);
    }

    /**
     * 设置有返回按钮+标题栏+右边按钮的ActionBar
     */
    public void setBackActionBar(int rightButtonShowPolicy){
        innerBuildCommonActionBar(rightButtonShowPolicy, R.drawable.actionbar_back);
    }

    /**
     * 构建有关闭按钮+标题栏的ActionBar
     */
    public void setCloseActionBar(){
        innerBuildCommonActionBar(RIGHTBUTTON_NONE, R.drawable.actionbar_close);
    }

    /**
     * 构建有关闭按钮+标题栏+右边按钮的ActionBar
     */
    public void setCloseActionBar(int rightButtonShowPolicy){
        innerBuildCommonActionBar(rightButtonShowPolicy, R.drawable.actionbar_close);
    }

    private void innerBuildCommonActionBar(int rightButtonShowPolicy,
                                           int leftButtonResID){
        mActionBar.setLeftButtonVisible(true)
                .setTitleViewVisible(true);

        switch(rightButtonShowPolicy){
            case RIGHTBUTTON_FIRST:
                mActionBar.setRightButton1Visible(true)
                        .setRightButton2Visible(false);
                break;
            case RIGHTBUTTON_SECOND:
                mActionBar.setRightButton1Visible(false)
                        .setRightButton2Visible(true);
                break;
            case RIGHTBUTTON_NONE:
                mActionBar.setRightButton1Visible(false)
                        .setRightButton2Visible(false);
                break;
            case RIGHTBUTTON_ALL:
                mActionBar.setRightButton1Visible(true)
                        .setRightButton2Visible(true);
                break;
        }

        //设置左边按钮图片
        mActionBar.setLeftButtonImageResource(leftButtonResID);
        //点击左边按钮返回
        mActionBar.setOnLeftButtonClickListener(v -> finishActivity());
    }

    /**
     * 关闭结束Activity
     */
    private void finishActivity(){
        mActivity.finish();
    }
}
