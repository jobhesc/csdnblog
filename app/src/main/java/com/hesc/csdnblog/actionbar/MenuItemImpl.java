package com.hesc.csdnblog.actionbar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hesc on 15/4/22.
 * 菜单项的实现类
 */
class MenuItemImpl implements MenuItem{
    private MenuItem.OnMenuItemClickListener itemClickListener;
    private Context context;
    private int itemId;
    private CharSequence title;
    private Drawable icon;
    private boolean visible;
    private boolean enabled;

    MenuItemImpl(Context context){
        this.context = context;
    }

    public MenuItem setitemId(int itemId){
        this.itemId = itemId;
        return this;
    }

    @Override
    public int getItemId() {
        return itemId;
    }

    @Override
    public int getGroupId() {
        return 0;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public MenuItem setTitle(CharSequence title) {
        this.title = title;
        return this;
    }

    @Override
    public MenuItem setTitle(int title) {
        return setTitle(context.getString(title));
    }

    @Override
    public CharSequence getTitle() {
        return title;
    }

    @Override
    public MenuItem setTitleCondensed(CharSequence title) {
        return this;
    }

    @Override
    public CharSequence getTitleCondensed() {
        return null;
    }

    @Override
    public MenuItem setIcon(Drawable icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public MenuItem setIcon(int iconRes) {
        return setIcon(context.getResources().getDrawable(iconRes));
    }

    @Override
    public Drawable getIcon() {
        return icon;
    }

    @Override
    public MenuItem setIntent(Intent intent) {
        return this;
    }

    @Override
    public Intent getIntent() {
        return null;
    }

    @Override
    public MenuItem setShortcut(char numericChar, char alphaChar) {
        return this;
    }

    @Override
    public MenuItem setNumericShortcut(char numericChar) {
        return this;
    }

    @Override
    public char getNumericShortcut() {
        return 0;
    }

    @Override
    public MenuItem setAlphabeticShortcut(char alphaChar) {
        return this;
    }

    @Override
    public char getAlphabeticShortcut() {
        return 0;
    }

    @Override
    public MenuItem setCheckable(boolean checkable) {
        return this;
    }

    @Override
    public boolean isCheckable() {
        return false;
    }

    @Override
    public MenuItem setChecked(boolean checked) {
        return this;
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public MenuItem setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public MenuItem setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean hasSubMenu() {
        return false;
    }

    @Override
    public SubMenu getSubMenu() {
        return null;
    }

    @Override
    public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
        this.itemClickListener = menuItemClickListener;
        return this;
    }

    public OnMenuItemClickListener getOnMenuItemClickListener(){
        return itemClickListener;
    }

    @Override
    public ContextMenu.ContextMenuInfo getMenuInfo() {
        return null;
    }

    @Override
    public void setShowAsAction(int actionEnum) {

    }

    @Override
    public MenuItem setShowAsActionFlags(int actionEnum) {
        return this;
    }

    @Override
    public MenuItem setActionView(View view) {
        return this;
    }

    @Override
    public MenuItem setActionView(int resId) {
        return this;
    }

    @Override
    public View getActionView() {
        return null;
    }

    @Override
    public MenuItem setActionProvider(ActionProvider actionProvider) {
        return this;
    }

    @Override
    public ActionProvider getActionProvider() {
        return null;
    }

    @Override
    public boolean expandActionView() {
        return false;
    }

    @Override
    public boolean collapseActionView() {
        return false;
    }

    @Override
    public boolean isActionViewExpanded() {
        return false;
    }

    @Override
    public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
        return this;
    }
}
