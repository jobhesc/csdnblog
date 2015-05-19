package com.hesc.csdnblog.actionbar;

import android.app.Activity;
import android.content.Context;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.view.Menu;

/**
 * Created by hesc on 15/4/22.
 */
class CommonActionBar extends BaseActionBar{
    protected Activity mActivity;

    public CommonActionBar(Context context){
        super(context);
        if(!(context instanceof Activity))
            throw new ClassCastException("context must be activity!");

        mActivity = (Activity)context;
        //设置默认标题
        setTitle(mActivity.getTitle().toString());
    }

}
