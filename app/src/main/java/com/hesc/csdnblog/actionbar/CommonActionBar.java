package com.hesc.csdnblog.actionbar;

import android.app.Activity;
import android.content.Context;

/**
 * Created by hesc on 15/4/22.
 */
class CommonActionBar extends BaseActionBar{
    public CommonActionBar(Context context){
        super(context);
        //设置默认标题
        if(context instanceof Activity)
            setTitle(((Activity)context).getTitle().toString());
    }
}
