package com.hesc.csdnblog.actionbar;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hesc on 15/4/23.
 */
public class ActionBarShowPolicy {
    private ActionBarShowMode mShowMode;
    private Activity mActivity;
    private IActionBar mActionBar;
    private BasePresenter mPresenter;

    public ActionBarShowPolicy(Activity activity, IActionBar actionBar) {
        mActivity = activity;
        mActionBar = actionBar;
        mPresenter = createPresenter();
    }

    private BasePresenter createPresenter() {
        if (mActivity instanceof ActionBarActivity)
            return new CompatPresenter(mActivity, mActionBar);
        else
            return new NormalPresenter(mActivity, mActionBar);
    }

    public ActionBarShowMode getActionBarShowMode() {
        return mShowMode;
    }

    /**
     * 设置actionbar的显示模式
     *
     * @param layoutResID
     * @param showMode
     * @throws ActionBarException
     */
    public void setActionBarShowMode(int layoutResID, ActionBarShowMode showMode) throws ActionBarException {
        View contentView = mActivity.getLayoutInflater().inflate(layoutResID, null);
        setActionBarShowMode(contentView, showMode);
    }

    /**
     * 设置actionbar的显示模式
     *
     * @param contentView
     * @param showMode
     * @throws ActionBarException
     */
    public void setActionBarShowMode(View contentView, ActionBarShowMode showMode) throws ActionBarException {
        setActionBarShowMode(contentView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ), showMode);
    }

    /**
     * 设置actionbar的显示模式
     * @param contentView
     * @param layoutParams
     * @param showMode
     * @throws ActionBarException
     */
    public void setActionBarShowMode(View contentView, ViewGroup.LayoutParams layoutParams,
                                     ActionBarShowMode showMode) throws ActionBarException {
        mShowMode = showMode;
        if (showMode == ActionBarShowMode.ACTIONBAR_EMBEDED) {
            mPresenter.showAsEmbedded(contentView, layoutParams);
        } else if (showMode == ActionBarShowMode.ACTIONBAR_INDEPENDENT) {
            mPresenter.showAsIndependent(contentView, layoutParams);
        }
    }
}
