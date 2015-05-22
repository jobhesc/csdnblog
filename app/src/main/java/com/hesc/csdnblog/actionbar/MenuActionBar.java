package com.hesc.csdnblog.actionbar;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;

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
        mMenuPopup = new MenuPopup(mActivity, mMenuBuilder, mRightButtonView);
        getMenuItemClickListener();
    }

    @Override
    public IActionBar setMenuLayout(int menuLayoutResId) {
        super.setMenuLayout(menuLayoutResId);
        mMenuBuilder.clear();
        new MenuInflater(mActivity).inflate(menuLayoutResId, mMenuBuilder);
        if(mMenuBuilder.size()>0) {
            super.setOnRightButtonClickListener(mOnClickListener);
        }
        return this;
    }

    @Override
    public IActionBar setOnRightButtonClickListener(View.OnClickListener listener) {
        if(mMenuBuilder.size()>0)
            super.setOnRightButtonClickListener(mOnClickListener);
        else
            super.setOnRightButtonClickListener(listener);
        return this;
    }

    private View.OnClickListener mOnClickListener = v->{
            if(v == mRightButtonView){  //点击右边的按钮
                if(mMenuPopup.isShowing()) {
                    mMenuPopup.dismiss();
                } else {
                    mMenuPopup.setOnMenuItemClickListener(getMenuItemClickListener());
                    mMenuPopup.show();
                }
            }
        };
}
