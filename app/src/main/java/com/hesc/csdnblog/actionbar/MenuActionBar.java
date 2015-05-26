package com.hesc.csdnblog.actionbar;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

/**
 * Created by hesc on 15/4/22.
 * 实现挂接菜单的ActionBar
 */
class MenuActionBar extends CommonActionBar {
    private Menu mMenuBuilder;
    private MenuPopup mMenuPopup;

    public MenuActionBar(Context context){
        super(context);
        mMenuBuilder = new MenuImpl(mActivity);
        mMenuPopup = new MenuPopup(mActivity, mMenuBuilder, mRightButton2View);
        getMenuItemClickListener();
    }

    @Override
    public IActionBar setMenuLayout(int menuLayoutResId) {
        super.setMenuLayout(menuLayoutResId);
        mMenuBuilder.clear();
        new MenuInflater(mActivity).inflate(menuLayoutResId, mMenuBuilder);
        if(mMenuBuilder.size()>0) {
            super.setOnRightButton2ClickListener(mOnClickListener);
        }
        return this;
    }

    @Override
    public IActionBar setOnRightButton2ClickListener(View.OnClickListener listener) {
        if(mMenuBuilder.size()>0)
            super.setOnRightButton2ClickListener(mOnClickListener);
        else
            super.setOnRightButton2ClickListener(listener);
        return this;
    }

    private View.OnClickListener mOnClickListener = v->{
            if(v == mRightButton2View){  //点击右边的按钮
                if(mMenuPopup.isShowing()) {
                    mMenuPopup.dismiss();
                } else {
                    mMenuPopup.setOnMenuItemClickListener(getMenuItemClickListener());
                    mMenuPopup.show();
                }
            }
        };
}
