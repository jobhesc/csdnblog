package com.hesc.csdnblog.actionbar;

import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;

/**
 * Created by hesc on 15/4/22.
 */
class MenuActionBar extends CommonActionBar implements View.OnClickListener {
    private Menu mMenuBuilder;

    public MenuActionBar(Context context){
        super(context);
        mMenuBuilder = new MenuImpl(mActivity);
    }

    @Override
    public IActionBar setMenuLayout(int menuLayoutResId) {
        super.setMenuLayout(menuLayoutResId);
        mMenuBuilder.clear();
        mActivity.getMenuInflater().inflate(menuLayoutResId, mMenuBuilder);
        if(mMenuBuilder.size()>0)
            super.setOnRightButtonClickListener(this);
        return this;
    }

    @Override
    public IActionBar setOnRightButtonClickListener(View.OnClickListener listener) {
        if(mMenuBuilder.size()>0)
            super.setOnRightButtonClickListener(this);
        else
            super.setOnRightButtonClickListener(listener);
        return this;
    }

    @Override
    public void onClick(View v) {
        if(v == mRightButtonView){  //点击右边的按钮

        }
    }
}
