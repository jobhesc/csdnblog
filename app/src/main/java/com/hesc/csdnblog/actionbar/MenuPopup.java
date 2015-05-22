package com.hesc.csdnblog.actionbar;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.support.v7.internal.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hesc.csdnblog.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hesc on 15/4/22.
 * 菜单弹出窗体实现类
 */
class MenuPopup {
    private Activity mActivity;
    //需要锚定到的视图
    private View mAnchorView;
    //菜单接口类，提供菜单项的信息集合
    private Menu mMenu;
    //带listview的弹出窗体类
    private ListPopupWindow mPopup;
    //菜单项数据适配类
    private MenuAdapter mMenuAdapter;
    //弹出窗体最大宽度
    private int mPopupMaxWidth;
    //菜单资源ID策略
    private MenuResPolicy mMenuResPolicy;
    //控件树观察类
    private ViewTreeObserver mTreeObserver;
    //菜单点击事件侦听类
    private MenuItem.OnMenuItemClickListener mMenuItemClickListener;

    public MenuPopup(Activity activity, Menu menu, View anchorView){
        mActivity = activity;
        mMenu = menu;
        mAnchorView = anchorView;
        mMenuResPolicy = new MenuResPolicy(activity);
        Resources res = activity.getResources();
        mPopupMaxWidth = Math.max(res.getDisplayMetrics().widthPixels / 2,
                res.getDimensionPixelSize(mMenuResPolicy.getConfigPrefDialogWidth()));
    }

    /**
     * 弹出菜单窗体
     */
    public void show(){
        if(isShowing()) return;
        mPopup = new ListPopupWindow(mActivity, null, mMenuResPolicy.getPopupMenuStyle());
        mPopup.setOnDismissListener(mPopupDismissListener);
        mPopup.setOnItemClickListener(mPopupItemClickListener);

        mMenuAdapter = new MenuAdapter(findVisibleItem(mMenu));
        mPopup.setAdapter(mMenuAdapter);
        //模态窗体，接受所有的按键消息
        mPopup.setModal(true);

        if(mTreeObserver == null){
            mTreeObserver = mAnchorView.getViewTreeObserver();
            mTreeObserver.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }
        mPopup.setAnchorView(mAnchorView);
        mPopup.setContentWidth(Math.min(measureContentWidth(mMenuAdapter), mPopupMaxWidth));
        mPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mPopup.show();
        mPopup.getListView().setOnKeyListener(mPopupKeyListener);
    }

    /**
     * 菜单窗体是否正在显示
     * @return
     */
    public boolean isShowing(){
        return mPopup != null && mPopup.isShowing();
    }

    /**
     * 关闭菜单窗体
     */
    public void dismiss(){
        if(isShowing()){
            mPopup.dismiss();
            mPopup = null;
        }
    }

    public void setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener l){
        mMenuItemClickListener = l;
    }

    public MenuItem.OnMenuItemClickListener getMenuItemClickListener(){
        return mMenuItemClickListener;
    }

    private void notifyMenuItemClick(MenuItem menuItem){
        if(mMenuItemClickListener != null){
            mMenuItemClickListener.onMenuItemClick(menuItem);
        }
    }

    /**
     * 测量ListView每一项的宽度，取最大值
     * @param adapter
     * @return
     */
    private int measureContentWidth(ListAdapter adapter){
        int maxWidth = 0;
        int itemType = 0;
        int count = adapter.getCount();
        View itemView = null;
        //测量ListView每一项的视图宽度，取最大值
        ViewGroup parentView = new FrameLayout(mActivity);
        for(int i = 0; i<count; i++){
            int positionType = adapter.getItemViewType(i);
            if(positionType != itemType){
                itemType = positionType;
                itemView = null;
            }

            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            itemView = adapter.getView(i, itemView, parentView);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            maxWidth = Math.max(maxWidth, itemView.getMeasuredWidth());
        }
        return maxWidth;
    }

    /**
     * 查找可见的菜单项
     * @param menu
     * @return
     */
    private List<MenuItem> findVisibleItem(Menu menu){
        List<MenuItem> visibleItems = new ArrayList<>();
        if(menu == null || menu.size() == 0) return visibleItems;

        int size = menu.size();
        for(int i = 0; i<size; i++){
            MenuItem menuItem = menu.getItem(i);
            if(menuItem.isVisible())
                visibleItems.add(menuItem);
        }
        return visibleItems;
    }

    /**
     * 弹出窗体ListView的按键事件侦听类
     */
    private View.OnKeyListener mPopupKeyListener = (v, keyCode, event)->{
        //按下menu键，关闭菜单窗体
        if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_MENU){
            dismiss();
            return true;
        }
        return false;
    };

    /**
     * 全局布局改变侦听类
     */
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = ()->{
        if(isShowing() && !mAnchorView.isShown()) {
            dismiss();
        }
    };

    /**
     * 弹出窗体关闭侦听类
     */
    private PopupWindow.OnDismissListener mPopupDismissListener = ()->{
        mPopup = null;
        if(mTreeObserver != null){
            if(!mTreeObserver.isAlive())
                mTreeObserver = mAnchorView.getViewTreeObserver();
            mTreeObserver.removeGlobalOnLayoutListener(mGlobalLayoutListener);
            mTreeObserver = null;
        }
    };

    /**
     * 弹出窗体菜单项点击侦听类
     */
    private AdapterView.OnItemClickListener mPopupItemClickListener = (parent, view, position, id)->{
        MenuItemImpl menuItem = (MenuItemImpl) mMenuAdapter.getItem(position);
        MenuItem.OnMenuItemClickListener listener = menuItem.getOnMenuItemClickListener();
        if(listener != null) {
            listener.onMenuItemClick(menuItem);
        }
        notifyMenuItemClick(menuItem);
    };

    private static class MenuResPolicy{
        private boolean mIsAppcompat;
        public MenuResPolicy(Activity activity){
            mIsAppcompat = activity instanceof ActionBarActivity;
        }

        public boolean isAppcompat(){
            return mIsAppcompat;
        }

        /**
         * 获取配置的弹出窗体的宽度资源ID
         * @return
         */
        public int getConfigPrefDialogWidth(){
            if(mIsAppcompat){
                return android.support.v7.appcompat.R.dimen.abc_config_prefDialogWidth;
            } else {
                return R.dimen.config_prefDialogWidth;
            }
        }

        /**
         * 获取弹出窗体的样式资源ID
         * @return
         */
        public int getPopupMenuStyle(){
            if(mIsAppcompat){
                return android.support.v7.appcompat.R.attr.popupMenuStyle;
            } else {
                return android.R.style.Widget_PopupMenu;
            }
        }
    }

    private class ViewHolder{
        public ImageView iconView;
        public TextView titleView;
    }

    private class MenuAdapter extends BaseAdapter{
        private List<MenuItem> mMenuItems;

        public MenuAdapter(List<MenuItem> menuItems){
            mMenuItems = menuItems;
        }

        @Override
        public int getCount() {
            return mMenuItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mMenuItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MenuItem menuItem = (MenuItem) getItem(position);
            ViewHolder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.actionbar_menu, null);
                holder = new ViewHolder();
                holder.iconView = (ImageView) convertView.findViewById(R.id.icon);
                holder.titleView = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.iconView.setImageDrawable(menuItem.getIcon());
            holder.titleView.setText(menuItem.getTitle());
            return convertView;
        }
    }
}
