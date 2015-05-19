package com.hesc.csdnblog.actionbar;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hesc on 15/4/22.
 */
class MenuImpl implements Menu{
    private Context context;
    private List<MenuItem> menuItems;
    MenuImpl(Context context){
        this.context = context;
        menuItems = new ArrayList<>();
    }

    private MenuItem addItem(int itemId){
        MenuItemImpl menuItem = new MenuItemImpl(context);
        menuItem.setitemId(itemId);
        menuItems.add(menuItem);
        return menuItem;
    }

    @Override
    public MenuItem add(CharSequence title) {
        return addItem(0).setTitle(title);
    }

    @Override
    public MenuItem add(int titleRes) {
        return addItem(0).setTitle(titleRes);
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        return addItem(itemId).setTitle(title);
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, int titleRes) {
        return addItem(itemId).setTitle(titleRes);
    }

    @Override
    public SubMenu addSubMenu(CharSequence title) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int titleRes) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
        return null;
    }

    @Override
    public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
        return 0;
    }

    @Override
    public void removeItem(int id) {
        MenuItem menuItem = findItem(id);
        if(menuItem!=null)
            menuItems.remove(menuItem);
    }

    @Override
    public void removeGroup(int groupId) {

    }

    @Override
    public void clear() {
        menuItems.clear();
    }

    @Override
    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {

    }

    @Override
    public void setGroupVisible(int group, boolean visible) {

    }

    @Override
    public void setGroupEnabled(int group, boolean enabled) {

    }

    @Override
    public boolean hasVisibleItems() {
        return false;
    }

    @Override
    public MenuItem findItem(int id) {
        for(MenuItem menuItem: menuItems){
            if(menuItem.getItemId() == id)
                return menuItem;
        }
        return null;
    }

    @Override
    public int size() {
        return menuItems.size();
    }

    @Override
    public MenuItem getItem(int index) {
        return menuItems.get(index);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
        return false;
    }

    @Override
    public boolean isShortcutKey(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean performIdentifierAction(int id, int flags) {
        return false;
    }

    @Override
    public void setQwertyMode(boolean isQwerty) {

    }
}
