package com.hesc.csdnblog.actionbar;

import android.content.Context;

/**
 * Created by hesc on 15/4/22.
 */
public class ActionBarFactory {
    /**
     * 创建普通的ActionBar
     * @param context
     * @return
     */
    public static IActionBar createCommonActionBar(Context context){
        return new CommonActionBar(context);
    }

    /**
     * 创建带菜单项的ActionBar
     * @param context
     * @return
     */
    public static IActionBar createMenuActionBar(Context context){
        return new MenuActionBar(context);
    }
}
