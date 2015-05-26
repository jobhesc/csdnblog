package com.hesc.csdnblog.actionbar;

import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by hesc on 15/4/22.
 */
public interface IActionBar {
    /**
     * 获取ActionBar的标题
     * @return
     */
    String getTitle();

    /**
     * 设置ActionBar的标题
     * @param title
     * @return
     */
    IActionBar setTitle(String title);

    /**
     * 获取左边按钮图像
     * @return
     */
    Drawable getLeftButtonDrawable();

    /**
     * 设置左边按钮图像
     * @param drawable
     * @return
     */
    IActionBar setLeftButtonDrawable(Drawable drawable);

    /**
     * 设置左边按钮图片资源ID
     * @param resID
     * @return
     */
    IActionBar setLeftButtonImageResource(int resID);
    /**
     * 获取右边按钮1图像
     * @return
     */
    Drawable getRightButton1Drawable();

    /**
     * 设置右边按钮1图像
     * @param drawable
     * @return
     */
    IActionBar setRightButton1Drawable(Drawable drawable);

    /**
     * 设置右边按钮1图片资源
     * @param resID
     * @return
     */
    IActionBar setRightButton1ImageResource(int resID);

    /**
     * 获取右边按钮2图像
     * @return
     */
    Drawable getRightButton2Drawable();

    /**
     * 设置右边按钮2图像
     * @param drawable
     * @return
     */
    IActionBar setRightButton2Drawable(Drawable drawable);

    /**
     * 设置右边按钮2图片资源
     * @param resID
     * @return
     */
    IActionBar setRightButton2ImageResource(int resID);
    /**
     * 设置左边按钮点击回调事件
     * @param listener
     * @return
     */
    IActionBar setOnLeftButtonClickListener(View.OnClickListener listener);

    /**
     * 设置右边按钮1点击回调事件
     * @param listener
     * @return
     */
    IActionBar setOnRightButton1ClickListener(View.OnClickListener listener);

    /**
     * 设置右边按钮2点击回调事件
     * @param listener
     * @return
     */
    IActionBar setOnRightButton2ClickListener(View.OnClickListener listener);
    /**
     * 设置左边按钮可见性
     * @param visible
     * @return
     */
    IActionBar setLeftButtonVisible(boolean visible);
    /**
     * 设置右边按钮1可见性
     * @param visible
     * @return
     */
    IActionBar setRightButton1Visible(boolean visible);
    /**
     * 设置右边按钮2可见性
     * @param visible
     * @return
     */
    IActionBar setRightButton2Visible(boolean visible);
    /**
     * 设置标题视图的可见性
     * @param visible
     * @return
     */
    IActionBar setTitleViewVisible(boolean visible);
    /**
     * 获取ActionBar的视图
     * @return
     */
    View getActionView();

    /**
     * 获取菜单布局资源ID
     * @return
     */
    int getMenuLayout();
    /**
     * 设置菜单布局资源ID
     * @param menuLayoutResId
     * @return
     */
    IActionBar setMenuLayout(int menuLayoutResId);

    /**
     * 获取菜单点击事件回调
     * @return
     */
    MenuItem.OnMenuItemClickListener getMenuItemClickListener();
    /**
     * 设置菜单点击事件回调
     * @param listener
     * @return
     */
    IActionBar setMenuItemClickListener(MenuItem.OnMenuItemClickListener listener);
}
